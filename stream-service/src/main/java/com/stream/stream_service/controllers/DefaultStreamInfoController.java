package com.stream.stream_service.controllers;

import com.stream.stream_service.DTO.DefaultStreamInfoRequest;
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

    

    @GetMapping
    public ResponseEntity<DefaultStreamInfo> getByChannelId(@PathVariable long userId) {
        return defaultStreamInfoService.getByChannelId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DefaultStreamInfo> upsert(@PathVariable long userId, @RequestBody DefaultStreamInfoRequest request) {
        DefaultStreamInfo savedInfo = defaultStreamInfoService.upsert(
            userId,
            request.getTitle(),
            request.getDescription()
        );
        return ResponseEntity.ok(savedInfo);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable long userId ) {
        defaultStreamInfoService.delete(userId);
        return ResponseEntity.ok().build();
    }
}
