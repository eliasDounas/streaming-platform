package com.example.morphine.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.example.grpc.ChannelServiceGrpc;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration gRPC client sous Spring Boot 3 / Java 17.
 */
@Configuration
public class GrpcClientConfig {

    @Value("${grpc.server.host:channel-app}")
    private String grpcHost;

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    private ManagedChannel channel;

    @Bean
    public ManagedChannel grpcManagedChannel() {
        channel = ManagedChannelBuilder
                .forAddress(grpcHost, grpcPort)
                .usePlaintext()
                .build();
        return channel;
    }

    @Bean
    public ChannelServiceGrpc.ChannelServiceBlockingStub channelServiceBlockingStub(ManagedChannel channel) {
        return ChannelServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void onDestroy() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
