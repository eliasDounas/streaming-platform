# -------------------------------
# 1) Build : Maven + protoc Linux
# -------------------------------

FROM maven:3.8.4-openjdk-17-slim AS build

# Installer protobuf-compiler (protoc) Linux
RUN apt-get update && \
    apt-get install -y protobuf-compiler && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copier le pom.xml, faire go-offline
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Compiler (sans tests), forcer protoc Linux
RUN mvn clean package -DskipTests \
    -DprotocExecutable=/usr/bin/protoc

# -------------------------------
# 2) Runtime : JRE léger
# -------------------------------
FROM openjdk:17-jdk-slim

COPY --from=build /app/target/*.jar /app/app.jar

# Exposer le port GraphQL ()
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
