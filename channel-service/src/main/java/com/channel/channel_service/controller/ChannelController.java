package com.channel.channel_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.channel.channel_service.DTO.ChannelCreateRequest;
import com.channel.channel_service.DTO.ChannelUpdateRequest;
import com.channel.channel_service.entities.Channel;
import com.channel.channel_service.services.ChannelService;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    @Autowired private ChannelService service;

    @PostMapping
    public ResponseEntity<Channel> create(@RequestBody ChannelCreateRequest request) {
        return ResponseEntity.ok(service.createChannel(
            request.getUserId(), request.getName(), request.getDescription(), request.getAvatarUrl()));
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<?> getPublic(@PathVariable String channelId) {
        return service.getPublicChannelInfo(channelId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/streamer/{userId}")
    public ResponseEntity<?> getPrivate(@PathVariable Long userId) {
        return service.getPrivateStreamerInfo(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{channelId}")
    public ResponseEntity<Channel> update(@PathVariable String channelId, @RequestBody ChannelUpdateRequest request) {
        return ResponseEntity.ok(service.updateChannel(channelId, request.getName(), request.getDescription(), request.getAvatarUrl()));
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable String channelId) {
        service.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }
}

