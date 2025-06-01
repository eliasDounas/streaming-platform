package com.stream.stream_service.controllers;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stream.stream_service.entities.Vod;
import com.stream.stream_service.services.VodService;

import java.util.List;

@RestController
@RequestMapping("/api/vods")
@RequiredArgsConstructor
public class VodController {

    private final VodService vodService;

    // 1. Create Vod from Stream ID
    @PostMapping("/create/{streamId}")
    public ResponseEntity<Vod> createVod(@PathVariable Long streamId) {
        Vod vod = vodService.createVod(streamId);
        return ResponseEntity.status(HttpStatus.CREATED).body(vod);
    }

    // 2. Update Vod (title and/or description)
    @PutMapping("/{vodId}")
    public ResponseEntity<Vod> updateVod(
            @PathVariable Long vodId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description
    ) {
        Vod updatedVod = vodService.updateVod(vodId, title, description);
        return ResponseEntity.ok(updatedVod);
    }

    // 3. Increment views
    @PostMapping("/{vodId}/views")
    public ResponseEntity<Vod> incrementViews(@PathVariable Long vodId) {
        Vod updatedVod = vodService.incrementViews(vodId);
        return ResponseEntity.ok(updatedVod);
    }

    // 4. Delete Vod
    @DeleteMapping("/{vodId}")
    public ResponseEntity<Void> deleteVod(
            @PathVariable Long vodId,
            @RequestParam String userId // e.g. from security context or passed directly
    ) {
        vodService.deleteVod(vodId, userId);
        return ResponseEntity.noContent().build();
    }

    // 5a. Get all VODs, optionally limited
    @GetMapping
    public ResponseEntity<List<Vod>> getVods(@RequestParam(required = false) Integer max) {
        List<Vod> vods = vodService.getVods(max);
        return ResponseEntity.ok(vods);
    }

    // 5b. Get VODs by channel ID, optionally limited
    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<Vod>> getVodsByChannelId(
            @PathVariable String channelId,
            @RequestParam(required = false) Integer max
    ) {
        List<Vod> vods = vodService.getVodsByChannelId(channelId, max);
        return ResponseEntity.ok(vods);
    }
}
