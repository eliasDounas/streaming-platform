package com.channel.channel_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.channel.channel_service.DTO.IvsEvent;
import com.channel.channel_service.entities.Channel;
import com.channel.channel_service.repositories.ChannelRepository;

@RestController
@RequestMapping("/api/ivs-events")
public class IvsEventsController {

    @Autowired
    private ChannelRepository channelRepository;

    @PostMapping
    public ResponseEntity<Void> handleIvsEvent(@RequestBody IvsEvent event) {
        String eventName = event.getDetail().getEvent_name();
        
        // Filter only "Stream Start" or "Stream End"
        if (!"Stream Start".equalsIgnoreCase(eventName) && !"Stream End".equalsIgnoreCase(eventName)) {
            return ResponseEntity.ok().build();
        }

        List<String> resources = event.getResources();
        if (resources == null || resources.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String channelArn = resources.get(0); // The ARN is always the first item
        System.out.println("Received event: " + eventName + " for channel ARN: " + channelArn);
        Optional<Channel> optionalChannel = channelRepository.findByArn(channelArn);
        if (optionalChannel.isPresent()) {
            Channel channel = optionalChannel.get();
            boolean isLive = "Stream Start".equalsIgnoreCase(eventName);
            channel.setLive(isLive);
            channelRepository.save(channel);
        }
        
        return ResponseEntity.ok().build();
    }
}
