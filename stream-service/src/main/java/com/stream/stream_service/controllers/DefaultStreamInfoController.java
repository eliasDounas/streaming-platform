package com.stream.stream_service.controllers;

import com.stream.stream_service.entities.DefaultStreamInfo;
import com.stream.stream_service.services.DefaultStreamInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/default-stream-info")
public class DefaultStreamInfoController {

    @Autowired
    private DefaultStreamInfoService defaultStreamInfoService;

    @PostMapping
    public ResponseEntity<DefaultStreamInfo> create(@RequestBody DefaultStreamInfo info) {
        return ResponseEntity.ok(defaultStreamInfoService.create(info));
    }

    @GetMapping("/{channelId}")
    public ResponseEntity<DefaultStreamInfo> getByChannelId(@PathVariable String channelId) {
        return defaultStreamInfoService.getByChannelId(channelId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{channelId}")
    public ResponseEntity<DefaultStreamInfo> update(@PathVariable String channelId, @RequestBody DefaultStreamInfo info) {
        return ResponseEntity.ok(defaultStreamInfoService.update(channelId, info));
    }

    @DeleteMapping("/{channelId}")
    public ResponseEntity<Void> delete(@PathVariable String channelId) {
        defaultStreamInfoService.delete(channelId);
        return ResponseEntity.ok().build();
    }
}
