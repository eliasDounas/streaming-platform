package com.stream.stream_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stream.stream_service.DTO.IvsEvent;
import com.stream.stream_service.services.StreamService;



@RestController
@RequestMapping("/api/ivs-events")
public class IvsEventsController {

    @Autowired
    private StreamService streamService;

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

        if ("Stream Start".equalsIgnoreCase(eventName)) {
            // Create a new stream when the stream starts
            streamService.createStream(channelArn);
        } else if ("Stream End".equalsIgnoreCase(eventName)) {
            // Handle stream end logic if needed
            // For example, you might want to update the stream status or save the end time
            streamService.endStream(channelArn);
        }
        return ResponseEntity.ok().build();
    }
}
