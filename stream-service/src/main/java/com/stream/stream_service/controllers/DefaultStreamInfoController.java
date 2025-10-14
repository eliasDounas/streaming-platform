package com.stream.stream_service.controllers;

import com.stream.stream_service.DTO.DefaultStreamInfoRequest;
import com.stream.stream_service.entities.DefaultStreamInfo;
import com.stream.stream_service.services.DefaultStreamInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/stream-service/default-stream-info")
public class DefaultStreamInfoController {

    @Autowired
    private DefaultStreamInfoService defaultStreamInfoService;   
    
    @GetMapping
    public ResponseEntity<DefaultStreamInfo> getByChannelId(@RequestHeader("X-User-Id") String userId) {
        return defaultStreamInfoService.getStreamInfoWithUserId(userId)
                                        .map(ResponseEntity::ok)
                                        .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<DefaultStreamInfo> upsert(@RequestHeader("X-User-Id") String userId, @RequestBody DefaultStreamInfoRequest request) {
        DefaultStreamInfo savedInfo = defaultStreamInfoService.upsert(
            userId,
            request.getTitle(),
            request.getDescription(),
            request.getCategory()
        );
        return ResponseEntity.ok(savedInfo);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestHeader("X-User-Id") String userId) {
        defaultStreamInfoService.delete(userId);
        return ResponseEntity.ok().build();
    }
}
