package com.stream.stream_service.controllers;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stream.stream_service.entities.Vod;
import com.stream.stream_service.services.VodService;

import java.util.List;

@RestController
@RequestMapping("/api/vods")
@CrossOrigin(origins = "*")
public class VodController {
    
    @Autowired
    private VodService vodService;
    
    @GetMapping
    public List<Vod> getAllVods() {
        return vodService.getAllVods();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vod> getVodById(@PathVariable Long id) {
        return vodService.getVodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/channel/{channelId}")
    public List<Vod> getVodsByChannelId(@PathVariable String channelId) {
        return vodService.getVodsByChannelId(channelId);
    }
    
    @GetMapping("/stream/{streamId}")
    public List<Vod> getVodsByStreamId(@PathVariable Long streamId) {
        return vodService.getVodsByStreamId(streamId);
    }
    
    @PostMapping
    public Vod createVod(@Valid @RequestBody Vod vod) {
        return vodService.createVod(vod);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Vod> updateVod(@PathVariable Long id, @Valid @RequestBody Vod vodDetails) {
        try {
            Vod updatedVod = vodService.updateVod(id, vodDetails);
            return ResponseEntity.ok(updatedVod);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVod(@PathVariable Long id) {
        vodService.deleteVod(id);
        return ResponseEntity.noContent().build();
    }
}