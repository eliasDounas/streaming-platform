package com.channel.channel_service.controller;

import java.util.Map;

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
import com.channel.channel_service.DTO.ChannelUpdateRequest;
import com.channel.channel_service.DTO.ChatRoomDTO;
import com.channel.channel_service.DTO.PublicChannelInfo;
import com.channel.channel_service.DTO.StreamConnectionInfo;
import com.channel.channel_service.entities.Channel;
import com.channel.channel_service.services.ChannelService;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    @Autowired private ChannelService channelService;

    @PostMapping    
    public ResponseEntity<ChannelCreateResponse> create(@RequestBody ChannelCreateRequest request) {
        
        Channel channel = channelService.createChannel(request.getUserId(), request.getName(), request.getDescription(), request.getAvatarUrl());

        ChannelCreateResponse response = new ChannelCreateResponse(channel.getChannelId(), "Channel created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<PublicChannelInfo> getPublic(@PathVariable String channelId) {
        return ResponseEntity.ok(channelService.getPublicChannelInfo(channelId));
    }

    @GetMapping("/{channelId}/chatroom")
    public ResponseEntity<ChatRoomDTO> getChatRoom(@PathVariable String channelId) {
        ChatRoomDTO dto = channelService.getChatRoomByChannelId(channelId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{channelId}/chatroom/token")
    public ResponseEntity<Map<String, String>> getChatToken(
            @RequestParam String chatRoomArn, 
            @RequestParam String userId) {
        String token = channelService.generateChatTokenIfValid(chatRoomArn, userId);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/streamer/{userId}")
    public ResponseEntity<StreamConnectionInfo> getStreamConnectionInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(channelService.getPrivateStreamerConnectionInfo(userId));
    }
    
    @PutMapping("/{channelId}")
    public ResponseEntity<Map<String, String>> update(@PathVariable String channelId, @RequestBody ChannelUpdateRequest request) {
        channelService.updateChannel(request.getUserId(), channelId, request.getName(), request.getDescription(), request.getAvatarUrl());
        return ResponseEntity.ok(Map.of("message", "Channel updated successfully."));
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Map<String, String>> delete(@RequestParam Long userId, @PathVariable String channelId) {
        channelService.deleteChannel(userId, channelId);
        return ResponseEntity.ok(Map.of("message", "Channel deleted successfully."));
    }
}

