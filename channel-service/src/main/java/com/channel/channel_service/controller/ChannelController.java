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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.channel.channel_service.DTO.ChannelCreateResponse;
import com.channel.channel_service.DTO.ChannelPreviewDTO;
import com.channel.channel_service.DTO.ChannelUpdateRequest;
import com.channel.channel_service.DTO.StreamConnectionInfo;
import com.channel.channel_service.entities.Channel;
import com.channel.channel_service.services.AvatarService;
import com.channel.channel_service.services.ChannelService;

@RestController
@RequestMapping("/channel-service")
public class ChannelController {
    @Autowired private ChannelService channelService;
    @Autowired private AvatarService avatarService;
    
    @PostMapping(value = "/channels", consumes = "multipart/form-data")    
    public ResponseEntity<ChannelCreateResponse> create(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "avatar", required = false) MultipartFile avatarFile) {
        
        try {
            // Validate inputs
            if (userId == null || userId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ChannelCreateResponse(null, "User ID is required"));
            }
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ChannelCreateResponse(null, "Channel name is required"));
            }

            // Upload avatar first to get the URL
            String avatarUrl = null;
            if (avatarFile != null && !avatarFile.isEmpty()) {
                avatarUrl = avatarService.uploadAvatar(avatarFile, userId);
            }

            // Create channel with the avatar URL
            Channel channel = channelService.createChannel(userId, name, description, avatarUrl);

            ChannelCreateResponse response = new ChannelCreateResponse(channel.getChannelId(), "Channel created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ChannelCreateResponse(null, e.getMessage()));
        } catch (Exception e) {
            // Log the actual error but don't expose internal details
            System.err.println("Channel creation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChannelCreateResponse(null, "Internal server error occurred during channel creation"));
        }
    }
      @GetMapping("/channels/{channelId}/chatroom/token")
    public ResponseEntity<Map<String, String>> getChatToken(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String channelId) {
        String token = channelService.generateChatTokenForChannel(channelId, userId);
        return ResponseEntity.ok(Map.of("token", token));
    }
    
    @GetMapping("/channels/connection-info")
    public ResponseEntity<StreamConnectionInfo> getStreamConnectionInfo(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(channelService.getPrivateStreamerConnectionInfo(userId));
    }
      
    @PutMapping("/channels/{channelId}")
    public ResponseEntity<Map<String, String>> updateChannel(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String channelId, 
            @RequestBody ChannelUpdateRequest request) {
        channelService.updateChannel(userId, channelId, request.getName(), request.getDescription(), request.getAvatarUrl());
        return ResponseEntity.ok(Map.of("message", "Channel updated successfully."));
    }    
    
    @DeleteMapping("/channels/{channelId}")
    public ResponseEntity<Map<String, String>> delete(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable String channelId) {
        channelService.deleteChannel(userId, channelId);
        return ResponseEntity.ok(Map.of("message", "Channel deleted successfully."));
    }    
    
    @GetMapping("/channels/my-channel")
    public ResponseEntity<ChannelPreviewDTO> getChannelByUserId(@RequestHeader("X-User-Id") String userId) {
        Optional<Channel> channelOpt = channelService.getChannelByUserId(userId);
        
        if (channelOpt.isPresent()) {
            Channel channel = channelOpt.get();
            ChannelPreviewDTO preview = new ChannelPreviewDTO(
                channel.getChannelId(),
                channel.getName(),
                channel.getPlaybackUrl(),
                channel.getAvatarUrl()
            );        return ResponseEntity.ok(preview);
        } else {
            return ResponseEntity.notFound().build();        }
    }
}