package com.channel.channel_service.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.channel.channel_service.entities.Channel;
import com.channel.channel_service.entities.ChatRoom;
import com.channel.channel_service.repositories.ChannelRepository;
// import com.channel.channel_service.repositories.ChatRoomRepository;

import software.amazon.awssdk.services.ivs.model.CreateChannelResponse;
import software.amazon.awssdk.services.ivs.model.StreamKey;
import software.amazon.awssdk.services.ivschat.model.CreateRoomResponse;

@Service
public class ChannelService {
    @Autowired private ChannelRepository channelRepository;
    // @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private AwsIvsService awsIvsService;

    public Channel createChannel(Long userId, String name, String description, String avatarUrl) {
        CreateChannelResponse ivsChannel = awsIvsService.createChannel(name);
        StreamKey streamKey = ivsChannel.streamKey(); // use this directly


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

    public Optional<Channel> getPublicChannelInfo(String channelId) {
        return channelRepository.findById(channelId);
    }

    public Optional<Channel> getPrivateStreamerInfo(Long userId) {
        return channelRepository.findByUserId(userId);
    }

    public Channel updateChannel(String channelId, String name, String description, String avatarUrl) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new RuntimeException("Channel not found"));
        channel.setName(name);
        channel.setDescription(description);
        channel.setAvatarUrl(avatarUrl);
        return channelRepository.save(channel);
    }

    public void deleteChannel(String channelId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(() -> new RuntimeException("Channel not found"));
        awsIvsService.deleteChannel(channel.getArn());
        channelRepository.delete(channel);
    }
}
