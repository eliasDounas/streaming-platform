package com.stream.stream_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stream.stream_service.DTO.IvsEvent;
import com.stream.stream_service.DTO.IvsRecordingEvent;
import com.stream.stream_service.services.StreamService;

@RestController
@RequestMapping("/api/ivs-events")
public class IvsEventsController {    @Autowired
    private StreamService streamService;

    @Value("${aws.s3.thumbnail-bucket}")
    private String s3BucketName;

    @Value("${aws.s3.region}")
    private String s3Region;

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

     
    @PostMapping("/recording")
    public ResponseEntity<Void> handleIvsRecording(@RequestBody IvsRecordingEvent recordingEvent) {
        String status = recordingEvent.getDetail().getRecording_status();

        if (!"Recording End".equalsIgnoreCase(status) && !"Recording Start".equalsIgnoreCase(status)) {
            return ResponseEntity.ok().build(); 
        }
        String bucket = recordingEvent.getDetail().getRecording_s3_bucket_name(); // "ivs-streams-archives"
        String region = recordingEvent.getRegion(); // "eu-west-1"
        String keyPrefix = recordingEvent.getDetail().getRecording_s3_key_prefix(); // path before media/
        String channelArn = recordingEvent.getResources().get(0); // The ARN is always the first item
          if ("Recording End".equalsIgnoreCase(status)) {
            // Construct the playback URL for the recording
    
            String playbackUrl = String.format(
                "https://%s.s3.%s.amazonaws.com/%s/media/hls/master.m3u8",
                bucket,
                region,
                keyPrefix
            );
    
            System.out.println("Playback URL: " + playbackUrl);
    
            // Update the VOD URL for the most recent finished stream
            streamService.updateStreamVodUrl(channelArn, playbackUrl);
    
            
        } else if ("Recording Start".equalsIgnoreCase(status)) {
            String thumbnailURL = String.format(
                "https://%s.s3.%s.amazonaws.com/%s/media/latest_thumbnail/thumb.jpg",
                bucket,
                region,
                keyPrefix
            );
    
            System.out.println("Thumbnail URL: " + thumbnailURL);
            
            // Update the thumbnail URL for the live stream
            streamService.updateStreamThumbnail(channelArn, thumbnailURL);
            
        }
    return ResponseEntity.ok().build();
    }

}