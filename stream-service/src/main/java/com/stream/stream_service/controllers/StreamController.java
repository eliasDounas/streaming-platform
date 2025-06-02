package com.stream.stream_service.controllers;

import com.stream.stream_service.DTO.StreamWithChannelDto;
import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.services.StreamService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/streams")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;

   
    // 3. Get the live stream by channelId
    @GetMapping("live/channel/{channelId}/")
    public ResponseEntity<StreamWithChannelDto> getLiveStreamByChannelId(@PathVariable String channelId) {
        return streamService.getLiveStreamByChannelId(channelId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. Get all live streams
    @GetMapping("/live")
    public ResponseEntity<List<StreamWithChannelDto>> getLiveStreams() {
        return ResponseEntity.ok(streamService.getLiveStreams());
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

    // 10. Delete stream
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStream(@PathVariable Long streamId, @PathVariable Long userId) {
        streamService.deleteStream(streamId, userId);
        return ResponseEntity.noContent().build();
    }
}
