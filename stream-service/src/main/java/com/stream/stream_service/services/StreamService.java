package com.stream.stream_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.repositories.StreamRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StreamService {
    
    @Autowired
    private StreamRepository streamRepository;
    
    public List<Stream> getAllStreams() {
        return streamRepository.findAll();
    }
    
    public Optional<Stream> getStreamById(Long id) {
        return streamRepository.findById(id);
    }
    
    public List<Stream> getStreamsByChannelId(String channelId) {
        return streamRepository.findByChannelId(channelId);
    }
    
    public List<Stream> getLiveStreams() {
        return streamRepository.findByIsLive(true);
    }
    
    public List<Stream> getLiveStreamsByChannelId(String channelId) {
        return streamRepository.findLiveStreamsByChannelId(channelId);
    }
    
    public List<Stream> getStreamsByViewCount() {
        return streamRepository.findAllOrderByViewCountDesc();
    }
    
    public Stream createStream(Stream stream) {
        if (stream.getStartedAt() == null) {
            stream.setStartedAt(LocalDateTime.now());
        }
        return streamRepository.save(stream);
    }
    
    public Stream updateStream(Long id, Stream streamDetails) {
        return streamRepository.findById(id)
                .map(stream -> {
                    stream.setTitle(streamDetails.getTitle());
                    stream.setDescription(streamDetails.getDescription());
                    stream.setThumbnailUrl(streamDetails.getThumbnailUrl());
                    stream.setIsLive(streamDetails.getIsLive());
                    stream.setViewers(streamDetails.getViewers());
                    if (streamDetails.getEndedAt() != null) {
                        stream.setEndedAt(streamDetails.getEndedAt());
                    }
                    return streamRepository.save(stream);
                })
                .orElseThrow(() -> new RuntimeException("Stream not found with id: " + id));
    }
    
    public Stream endStream(Long id) {
        return streamRepository.findById(id)
                .map(stream -> {
                    stream.setIsLive(false);
                    stream.setEndedAt(LocalDateTime.now());
                    return streamRepository.save(stream);
                })
                .orElseThrow(() -> new RuntimeException("Stream not found with id: " + id));
    }
    
    public void deleteStream(Long id) {
        streamRepository.deleteById(id);
    }
}