package com.channel.channel_service.controller;

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

    @PostMapping("/ivs/events")
    public ResponseEntity<Void> handleIvsEvent(@RequestBody IvsEvent event) {
        String channelArn = event.getDetail().getChannelArn();
        String streamState = event.getDetail().getStreamState();

        Optional<Channel> optionalChannel = channelRepository.findByArn(channelArn);
        if (optionalChannel.isPresent()) {
            Channel channel = optionalChannel.get();
            channel.setLive("LIVE".equalsIgnoreCase(streamState));
            channelRepository.save(channel);
        }

        return ResponseEntity.ok().build();
    }

}
