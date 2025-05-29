package com.channel.channel_service.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.channel.channel_service.entities.Channel;
import com.channel.channel_service.entities.ChatRoom;
import com.channel.channel_service.DTO.ChannelPreviewDTO;
import com.channel.channel_service.DTO.ChatRoomDTO;
import com.channel.channel_service.DTO.PublicChannelInfo;
import com.channel.channel_service.DTO.StreamConnectionInfo;
import com.channel.channel_service.exceptions.ChannelNotFoundException;
import com.channel.channel_service.exceptions.ChatRoomNotFoundException;
import com.channel.channel_service.exceptions.UnauthorizedException;
import com.channel.channel_service.exceptions.UserAlreadyHasAChannelException;
import com.channel.channel_service.repositories.ChannelRepository;
import com.channel.channel_service.repositories.ChatRoomRepository;

import jakarta.transaction.Transactional;
import software.amazon.awssdk.services.ivs.model.CreateChannelResponse;
import software.amazon.awssdk.services.ivs.model.StreamKey;
import software.amazon.awssdk.services.ivschat.model.CreateRoomResponse;

@Service
public class ChannelService {
    @Autowired private ChannelRepository channelRepository;
    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private AwsIvsService awsIvsService;

    @Transactional
    public Channel createChannel(Long userId, String name, String description, String avatarUrl) {
        
        if (channelRepository.findByUserId(userId).isPresent()) {
            throw new UserAlreadyHasAChannelException("This User already has a channel.");
        }

        CreateChannelResponse ivsChannel = awsIvsService.createChannel(name);
        StreamKey streamKey = ivsChannel.streamKey(); 

        CreateRoomResponse chatRoom = awsIvsService.createChatRoom(name + "-chat");

        Channel channel = new Channel();
        channel.setChannelId(UUID.randomUUID().toString());
        channel.setName(name);
        channel.setDescription(description);
        channel.setLive(false);
        channel.setArn(ivsChannel.channel().arn());
        channel.setStreamKey(streamKey.value());
        channel.setStreamKeyArn(streamKey.arn());
        channel.setIngestEndpoint(ivsChannel.channel().ingestEndpoint());
        channel.setPlaybackUrl(ivsChannel.channel().playbackUrl());
        channel.setUserId(userId);
        channel.setAvatarUrl(avatarUrl);
        channel.setCreatedAt(LocalDateTime.now());

        ChatRoom room = new ChatRoom();
        room.setArn(chatRoom.arn());
        room.setEndpoint(chatRoom.loggingConfigurationIdentifiers().toString());
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        room.setChannel(channel);

        channel.setChatRoom(room);


        return channelRepository.save(channel);
    }

    public PublicChannelInfo getPublicChannelInfo(String channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException("Channel not found"));

        return new PublicChannelInfo(
            channel.getChannelId(),
            channel.getName(),
            channel.getDescription(),
            channel.isLive(),
            channel.getPlaybackUrl(),
            channel.getAvatarUrl()
        );
    }

    public ChatRoomDTO getChatRoomByChannelId(String channelId) {
        ChatRoom chatRoom = chatRoomRepository.findByChannel_ChannelId(channelId)
            .orElseThrow(() -> new ChatRoomNotFoundException("ChatRoom not found "));

        return new ChatRoomDTO(
            chatRoom.getId(),
            chatRoom.getArn(),
            chatRoom.getEndpoint()
        );
}
   
    public String generateChatTokenIfValid(String chatRoomArn, String userId) {

        // Validate if chatRoomArn exists in DB
        chatRoomRepository.findByArn(chatRoomArn)
            .orElseThrow(() -> new ChatRoomNotFoundException("ChatRoom ARN not found"));
        
        // (Optional) You can add more checks, e.g., user permissions here

        // If valid, generate token from awsIvsService
        return awsIvsService.createChatToken(chatRoomArn, userId);
    } 
    public StreamConnectionInfo getPrivateStreamerConnectionInfo(Long userId) {
        Channel channel = channelRepository.findByUserId(userId)
            .orElseThrow(() -> new ChannelNotFoundException("Channel not found for user: " + userId));

        return new StreamConnectionInfo(
            channel.getChannelId(),
            channel.getStreamKey(),
            channel.getIngestEndpoint()
        );
    }


    public Channel updateChannel(Long userId, String channelId, String name, String description, String avatarUrl) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException("Channel not found"));
        
        if (!channel.getUserId().equals(userId)) {
            throw new UnauthorizedException("User not authorized");
        }
        if (name != null) {
        channel.setName(name);
        }
        if (description != null) {
            channel.setDescription(description);
        }
        if (avatarUrl != null) {
            channel.setAvatarUrl(avatarUrl);
        }
        return channelRepository.save(channel);
    }

    public void deleteChannel(Long userId, String channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new ChannelNotFoundException("Channel not found"));
        
        if (!channel.getUserId().equals(userId)) {
            throw new UnauthorizedException("User not authorized");
        }

        awsIvsService.deleteChannel(channel.getArn());
        
        if (channel.getChatRoom() != null) {
            awsIvsService.deleteChatRoom(channel.getChatRoom().getArn());
        }
        
        channelRepository.delete(channel);
    }

    public List<ChannelPreviewDTO> getLiveChannels() {
        // Use repository method that returns max 10 channels where isLive == true
        List<Channel> channels = channelRepository.findTop10ByIsLiveTrue();
    
        // Convert entities to DTOs (assuming ChannelPreviewDTO has an appropriate constructor or builder)
        return channels.stream()
                .map(channel -> new ChannelPreviewDTO(
                    channel.getChannelId(),
                    channel.getName(),
                    channel.getPlaybackUrl(),
                    channel.getAvatarUrl()
                ))
                .collect(Collectors.toList());
    }
    
    public List<String> getAllChannelIds() {
        return channelRepository.findAllChannelIds();
    }
    
    public List<ChannelPreviewDTO> getChannelPreviewsByIds(List<String> ids) {
    return channelRepository.findAllById(ids)
                            .stream()
                            .map(channel -> new ChannelPreviewDTO(
                                channel.getChannelId(),
                                channel.getName(),
                                channel.getPlaybackUrl(),
                                channel.getAvatarUrl()
                            ))
                            .toList();
    }

}