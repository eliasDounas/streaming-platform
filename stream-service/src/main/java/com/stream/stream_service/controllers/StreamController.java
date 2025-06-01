package com.stream.stream_service.controllers;

import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.services.StreamService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

    // 1. Create stream
    @PostMapping("/create/{channelId}")
    public ResponseEntity<Stream> createStream(@PathVariable String channelId) {
        Stream stream = streamService.createStream(channelId);
        return ResponseEntity.status(HttpStatus.CREATED).body(stream);
    }

    // 2. Get all streams by channelId
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<Stream>> getStreamsByChannelId(@PathVariable String channelId) {
        return ResponseEntity.ok(streamService.getStreamsByChannelId(channelId));
    }

    // 3. Get the live stream by channelId
    @GetMapping("/channel/{channelId}/live")
    public ResponseEntity<Stream> getLiveStreamByChannelId(@PathVariable String channelId) {
        return streamService.getLiveStreamByChannelId(channelId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Get all live streams
    @GetMapping("/live")
    public ResponseEntity<List<Stream>> getLiveStreams() {
        return ResponseEntity.ok(streamService.getLiveStreams());
    }

    // 5. Update live stream title/description
    @PutMapping("/channel/{channelId}")
    public ResponseEntity<Stream> updateStream(
            @PathVariable String channelId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description
    ) {
        return streamService.updateStream(channelId, title, description)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. Increment viewers
    @PostMapping("/{id}/viewers/increment")
    public ResponseEntity<Stream> incrementViewers(@PathVariable Long id) {
        return streamService.incrementViewers(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 7. Decrement viewers
    @PostMapping("/{id}/viewers/decrement")
    public ResponseEntity<Stream> decrementViewers(@PathVariable Long id) {
        return streamService.decrementViewers(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 8. Get viewers count
    @GetMapping("/{id}/viewers")
    public ResponseEntity<Long> getViewersCount(@PathVariable Long id) {
        return streamService.getViewersCount(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 9. End a stream
    @PostMapping("/{id}/end")
    public ResponseEntity<Stream> endStream(@PathVariable Long id) {
        Stream stream = streamService.endStream(id);
        return ResponseEntity.ok(stream);
    }

    // 10. Delete stream
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStream(@PathVariable Long id) {
        streamService.deleteStream(id);
        return ResponseEntity.noContent().build();
    }
}
