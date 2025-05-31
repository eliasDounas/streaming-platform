package com.stream.stream_service.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stream.stream_service.entities.Vod;
import com.stream.stream_service.repositories.VodRepository;

import java.util.List;
import java.util.Optional;

@Service
public class VodService {
    
    @Autowired
    private VodRepository vodRepository;
    
    public List<Vod> getAllVods() {
        return vodRepository.findAllOrderByCreatedAtDesc();
    }
    
    public Optional<Vod> getVodById(Long id) {
        return vodRepository.findById(id);
    }
    
    public List<Vod> getVodsByChannelId(String channelId) {
        return vodRepository.findByChannelIdOrderByCreatedAtDesc(channelId);
    }
    
    public List<Vod> getVodsByStreamId(Long streamId) {
        return vodRepository.findByStreamId(streamId);
    }
    
    public Vod createVod(Vod vod) {
        return vodRepository.save(vod);
    }
    
    public Vod updateVod(Long id, Vod vodDetails) {
        return vodRepository.findById(id)
                .map(vod -> {
                    vod.setTitle(vodDetails.getTitle());
                    vod.setDescription(vodDetails.getDescription());
                    vod.setVodUrl(vodDetails.getVodUrl());
                    vod.setDuration(vodDetails.getDuration());
                    vod.setThumbnailUrl(vodDetails.getThumbnailUrl());
                    return vodRepository.save(vod);
                })
                .orElseThrow(() -> new RuntimeException("VOD not found with id: " + id));
    }
    
    public void deleteVod(Long id) {
        vodRepository.deleteById(id);
    }
}
