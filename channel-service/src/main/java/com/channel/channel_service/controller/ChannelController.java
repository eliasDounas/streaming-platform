package com.channel.channel_service.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.channel.channel_service.DTO.ChannelCreateRequest;
import com.channel.channel_service.DTO.ChannelCreateResponse;
import com.channel.channel_service.DTO.ChannelPreviewDTO;
import com.channel.channel_service.DTO.ChannelUpdateRequest;
import com.channel.channel_service.DTO.StreamConnectionInfo;
import com.channel.channel_service.entities.Channel;
import com.channel.channel_service.services.ChannelService;

@RestController
@RequestMapping("/channel-service")
public class ChannelController {
    @Autowired private ChannelService channelService;

    @PostMapping("/channels")    
    public ResponseEntity<ChannelCreateResponse> create(@RequestBody ChannelCreateRequest request) {
        
        Channel channel = channelService.createChannel(request.getUserId(), request.getName(), request.getDescription(), request.getAvatarUrl());

        ChannelCreateResponse response = new ChannelCreateResponse(channel.getChannelId(), "Channel created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/channels/{channelId}/chatroom/token")
    public ResponseEntity<Map<String, String>> getChatToken(
            @RequestParam String chatRoomArn, 
            @RequestParam String userId) {
        String token = channelService.generateChatTokenIfValid(chatRoomArn, userId);
        return ResponseEntity.ok(Map.of("token", token));
    }    
    @GetMapping("/channels/streamer/{userId}")
    public ResponseEntity<StreamConnectionInfo> getStreamConnectionInfo(@PathVariable String userId) {
        return ResponseEntity.ok(channelService.getPrivateStreamerConnectionInfo(userId));
    }
    
    @PutMapping("/channels/{channelId}")
    public ResponseEntity<Map<String, String>> updateChannel(@PathVariable String channelId, @RequestBody ChannelUpdateRequest request) {
        channelService.updateChannel(request.getUserId(), channelId, request.getName(), request.getDescription(), request.getAvatarUrl());
        return ResponseEntity.ok(Map.of("message", "Channel updated successfully."));
    }

    @DeleteMapping("/channels/{channelId}")
    public ResponseEntity<Map<String, String>> delete(@RequestParam String userId, @PathVariable String channelId) {
        channelService.deleteChannel(userId, channelId);
        return ResponseEntity.ok(Map.of("message", "Channel deleted successfully."));
    }

    @GetMapping("/channels/user/{userId}")
    public ResponseEntity<ChannelPreviewDTO> getChannelByUserId(@PathVariable String userId) {
        Optional<Channel> channelOpt = channelService.getChannelByUserId(userId);
        
        if (channelOpt.isPresent()) {
            Channel channel = channelOpt.get();
            ChannelPreviewDTO preview = new ChannelPreviewDTO(
                channel.getChannelId(),
                channel.getName(),
                channel.getPlaybackUrl(),
                channel.getAvatarUrl()
            );
            return ResponseEntity.ok(preview);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}