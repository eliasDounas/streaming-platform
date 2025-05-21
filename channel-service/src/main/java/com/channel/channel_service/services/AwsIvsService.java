package com.channel.channel_service.services;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.ivs.IvsClient;
import software.amazon.awssdk.services.ivs.model.ChannelLatencyMode;
import software.amazon.awssdk.services.ivs.model.ChannelType;
import software.amazon.awssdk.services.ivs.model.CreateChannelRequest;
import software.amazon.awssdk.services.ivs.model.CreateChannelResponse;
import software.amazon.awssdk.services.ivs.model.CreateStreamKeyRequest;
import software.amazon.awssdk.services.ivs.model.CreateStreamKeyResponse;
import software.amazon.awssdk.services.ivs.model.DeleteChannelRequest;
import software.amazon.awssdk.services.ivschat.IvschatClient;
import software.amazon.awssdk.services.ivschat.model.CreateRoomRequest;
import software.amazon.awssdk.services.ivschat.model.CreateRoomResponse;

@Service
public class AwsIvsService {
    private final IvsClient ivsClient;
    private final IvschatClient chatClient;

    public AwsIvsService(IvsClient ivsClient, IvschatClient chatClient) {
        this.ivsClient = ivsClient;
        this.chatClient = chatClient;
    }

    public CreateChannelResponse createChannel(String name) {
        CreateChannelRequest request = CreateChannelRequest.builder()
            .name(name)
            .latencyMode(ChannelLatencyMode.LOW) 
            .type(ChannelType.STANDARD)          
            .authorized(false)                
            .build();
        return ivsClient.createChannel(request);
    }

    public CreateStreamKeyResponse createStreamKey(String channelArn) {
        return ivsClient.createStreamKey(CreateStreamKeyRequest.builder()
            .channelArn(channelArn)
            .build());
    }

    public CreateRoomResponse createChatRoom(String name) {
        return chatClient.createRoom(CreateRoomRequest.builder()
            .name(name )
            .build());
    }

    public void deleteChannel(String arn) {
        ivsClient.deleteChannel(DeleteChannelRequest.builder().arn(arn).build());
    }
}
