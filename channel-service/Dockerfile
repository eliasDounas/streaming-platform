# Multi-stage Dockerfile for Spring Boot Service
# Stage 1: Build stage with Maven (using Ubuntu for better protoc compatibility)
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy JAR file from builder stage
COPY --from=builder /app/target/channel-service-*.jar app.jar

# Install curl (optional, can be removed if not needed)
RUN apk add --no-cache curl

# Run the application with default profile
CMD ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-jar", "/app/app.jar"]

# Expose the app port
EXPOSE 8080