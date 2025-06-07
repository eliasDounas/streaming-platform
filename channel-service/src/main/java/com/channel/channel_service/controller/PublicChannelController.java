package com.channel.channel_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.channel.channel_service.DTO.ChannelPreviewDTO;
import com.channel.channel_service.DTO.ChatRoomDTO;
import com.channel.channel_service.DTO.PublicChannelInfo;
import com.channel.channel_service.services.ChannelService;

@RestController
@RequestMapping("/channel-service/public")
public class PublicChannelController {
    
    @Autowired 
    private ChannelService channelService;

    @GetMapping("/{channelId}")
    public ResponseEntity<PublicChannelInfo> getPublic(@PathVariable String channelId) {
        return ResponseEntity.ok(channelService.getPublicChannelInfo(channelId));
    }

    @GetMapping("/{channelId}/chatroom")
    public ResponseEntity<ChatRoomDTO> getChatRoom(@PathVariable String channelId) {
        ChatRoomDTO dto = channelService.getChatRoomByChannelId(channelId);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/live")
    public ResponseEntity<List<ChannelPreviewDTO>> getLiveChannels() {
        List<ChannelPreviewDTO> liveChannels = channelService.getLiveChannels();
        return ResponseEntity.ok(liveChannels);
    }
}
