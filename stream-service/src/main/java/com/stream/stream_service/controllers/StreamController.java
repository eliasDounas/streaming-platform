package com.stream.stream_service.controllers;

import com.stream.stream_service.DTO.StreamWithChannelDto;
import com.stream.stream_service.DTO.PaginatedStreamResponse;
import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.services.StreamService;
import com.stream.stream_service.services.StreamQueryService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stream-service/public")
@RequiredArgsConstructor
public class StreamController {

    private final StreamService streamService;
    private final StreamQueryService streamQueryService;

    // Get live stream by channel ID
    @GetMapping("/channels/{channelId}/live")
    public ResponseEntity<StreamWithChannelDto> getLiveStream(@PathVariable String channelId) {
        return streamQueryService.getLiveStreamByChannelId(channelId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all live streams
    @GetMapping("/livestreams")
    public ResponseEntity<List<StreamWithChannelDto>> getAllLiveStreams() {
        return ResponseEntity.ok(streamQueryService.getLiveStreams());
    }
    

    // Get finished streams with pagination metadata
    @GetMapping("/channels/{channelId}/finished")
    public ResponseEntity<PaginatedStreamResponse<Stream>> getFinishedStreamsWithMeta(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedStreamResponse<Stream> response = streamQueryService.getFinishedStreamsWithMetadata(channelId, page, size);
        return ResponseEntity.ok(response);
    }    
    
    // Get all finished streams sorted by views with channel info and pagination metadata
    @GetMapping("/vods/popular")
    public ResponseEntity<PaginatedStreamResponse<StreamWithChannelDto>> getPopularFinishedStreams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedStreamResponse<StreamWithChannelDto> response = streamQueryService.getFinishedStreamsWithChannelInfo(page, size);
        return ResponseEntity.ok(response);
    }
      // Increment stream viewers
    @PostMapping("/{id}/viewers")
    public ResponseEntity<Stream> incrementViewers(@PathVariable String id) {
        return streamService.incrementViewers(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get viewers count
    @GetMapping("/{id}/viewers")
    public ResponseEntity<Long> getViewersCount(@PathVariable String id) {
        return streamService.getViewersCount(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }    
    
    // Get stream by ID with channel information
    @GetMapping("/{streamId}")
    public ResponseEntity<StreamWithChannelDto> getStreamById(@PathVariable String streamId) {
        return streamQueryService.getStreamWithChannelById(streamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // // Delete stream
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteStream(@PathVariable String id, @RequestParam String userId) {
    //     streamService.deleteStream(id, userId);
    //     return ResponseEntity.noContent().build();
    // }
}
