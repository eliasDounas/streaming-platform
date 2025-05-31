package com.stream.stream_service.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.services.StreamService;

import java.util.List;

@RestController
@RequestMapping("/api/streams")
@CrossOrigin(origins = "*")
public class StreamController {
    
    @Autowired
    private StreamService streamService;
    
    @GetMapping
    public List<Stream> getAllStreams() {
        return streamService.getAllStreams();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Stream> getStreamById(@PathVariable Long id) {
        return streamService.getStreamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/channel/{channelId}")
    public List<Stream> getStreamsByChannelId(@PathVariable String channelId) {
        return streamService.getStreamsByChannelId(channelId);
    }
    
    @GetMapping("/live")
    public List<Stream> getLiveStreams() {
        return streamService.getLiveStreams();
    }
    
    @GetMapping("/live/channel/{channelId}")
    public List<Stream> getLiveStreamsByChannelId(@PathVariable String channelId) {
        return streamService.getLiveStreamsByChannelId(channelId);
    }
    
    @GetMapping("/popular")
    public List<Stream> getPopularStreams() {
        return streamService.getStreamsByViewCount();
    }
    
    @PostMapping
    public Stream createStream(@Valid @RequestBody Stream stream) {
        return streamService.createStream(stream);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Stream> updateStream(@PathVariable Long id, @Valid @RequestBody Stream streamDetails) {
        try {
            Stream updatedStream = streamService.updateStream(id, streamDetails);
            return ResponseEntity.ok(updatedStream);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/end")
    public ResponseEntity<Stream> endStream(@PathVariable Long id) {
        try {
            Stream endedStream = streamService.endStream(id);
            return ResponseEntity.ok(endedStream);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStream(@PathVariable Long id) {
        streamService.deleteStream(id);
        return ResponseEntity.noContent().build();
    }
}