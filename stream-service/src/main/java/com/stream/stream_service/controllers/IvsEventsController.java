package com.stream.stream_service.controllers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        System.out.println("=== IVS Event Received ===");
        System.out.println("Full event: " + event);
        
        String eventName = event.getDetail().getEvent_name();
        System.out.println("Event name: " + eventName);
        
        // Filter only "Stream Start" or "Stream End"
        if (!"Stream Start".equalsIgnoreCase(eventName) && !"Stream End".equalsIgnoreCase(eventName)) {
            System.out.println("Ignoring event: " + eventName);
            return ResponseEntity.ok().build();
        }

        List<String> resources = event.getResources();
        if (resources == null || resources.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }        String channelArn = resources.get(0); // The ARN is always the first item
        String awsStreamId = event.getDetail().getStream_id(); // Get AWS stream ID
        System.out.println("Received event: " + eventName + " for channel ARN: " + channelArn + ", stream ID: " + awsStreamId);        if ("Stream Start".equalsIgnoreCase(eventName)) {
            streamService.createStream(channelArn, awsStreamId);
        } else if ("Stream End".equalsIgnoreCase(eventName)) {
            streamService.endStreamByAwsStreamId(awsStreamId);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recording")
    public ResponseEntity<Void> handleIvsRecording(@RequestBody IvsRecordingEvent recordingEvent) {
        System.out.println("=== IVS Recording Event Received ===");
        System.out.println("Full recording event: " + recordingEvent);
        
        String status = recordingEvent.getDetail().getRecording_status();
        System.out.println("Recording status: " + status);

        if (!"Recording End".equalsIgnoreCase(status) && !"Recording Start".equalsIgnoreCase(status)) {
            return ResponseEntity.ok().build(); 
        }        String bucket = recordingEvent.getDetail().getRecording_s3_bucket_name(); // "ivs-streams-archives"
        String region = recordingEvent.getRegion(); // "eu-west-1"
        String keyPrefix = recordingEvent.getDetail().getRecording_s3_key_prefix(); // path before media/
        String awsStreamId = recordingEvent.getDetail().getStream_id(); // AWS stream ID
          if ("Recording End".equalsIgnoreCase(status)) {
            // Construct the playback URL for the recording
    
            String playbackUrl = String.format(
                "https://%s.s3.%s.amazonaws.com/%s/media/hls/master.m3u8",
                bucket,
                region,
                keyPrefix
            );
    
            System.out.println("Playback URL: " + playbackUrl);
            System.out.println("AWS Stream ID: " + awsStreamId);
    
            // Update the VOD URL for the specific stream using AWS stream ID
            streamService.updateStreamVodUrlByAwsStreamId(awsStreamId, playbackUrl);        } else if ("Recording Start".equalsIgnoreCase(status)) {
            String thumbnailURL = String.format(
                "https://%s.s3.%s.amazonaws.com/%s/media/latest_thumbnail/thumb.jpg",
                bucket,
                region,
                keyPrefix
            );
    
            System.out.println("Thumbnail URL: " + thumbnailURL);
            System.out.println("AWS Stream ID: " + awsStreamId);
            
            // Update the thumbnail URL for the specific stream with 1-minute delay
            updateStreamThumbnailWithDelay(awsStreamId, thumbnailURL);
            
        }
    return ResponseEntity.ok().build();
    }    /**
     * Updates the stream thumbnail with a 30-second delay using AWS stream ID
     */
    private void updateStreamThumbnailWithDelay(String awsStreamId, String thumbnailURL) {
        CompletableFuture.delayedExecutor(30, java.util.concurrent.TimeUnit.SECONDS)
            .execute(() -> {
                System.out.println("Updating thumbnail after 30-second delay for stream ID: " + awsStreamId);
                streamService.updateStreamThumbnailByAwsStreamId(awsStreamId, thumbnailURL);
        });
    }

}