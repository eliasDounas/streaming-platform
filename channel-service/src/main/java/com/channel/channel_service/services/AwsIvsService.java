package com.channel.channel_service.services;

import org.springframework.beans.factory.annotation.Value;
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
import software.amazon.awssdk.services.ivschat.model.CreateChatTokenRequest;
import software.amazon.awssdk.services.ivschat.model.CreateChatTokenResponse;
import software.amazon.awssdk.services.ivschat.model.CreateRoomRequest;
import software.amazon.awssdk.services.ivschat.model.CreateRoomResponse;
import software.amazon.awssdk.services.ivschat.model.DeleteRoomRequest;

@Service
public class AwsIvsService {
    
    @Value("${aws.ivs.recording-config-arn}")
    private String recordingConfigArn;

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
            .recordingConfigurationArn(recordingConfigArn)               
            .build();
        return ivsClient.createChannel(request);
    }

    /**
     * Create a stream key for a channel.
     * 
     * @param channelArn ARN of the channel
     * @return Stream key response containing the stream key and its ARN
     */
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

    /**
     * Create a chat token for a user to send messages in a chat room.
     * 
     * @param chatRoomArn ARN of the chat room
     * @param userId The user ID or username that will be in the chat
     * @return Chat token string (JWT)
     */
    public String createChatToken(String chatRoomArn, String userId) {
        CreateChatTokenRequest request = CreateChatTokenRequest.builder()
            .roomIdentifier(chatRoomArn)  
            .userId(userId)               
            .sessionDurationInMinutes(180) 
            .build();

        CreateChatTokenResponse response = chatClient.createChatToken(request);

        return response.token();
    }

    public void deleteChannel(String arn) {
        ivsClient.deleteChannel(DeleteChannelRequest.builder().arn(arn).build());
    }

    public void deleteChatRoom(String chatRoomArn) {
    chatClient.deleteRoom(DeleteRoomRequest.builder()
        .identifier(chatRoomArn)
        .build());
    }

}
