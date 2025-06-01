package com.stream.stream_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.stream.stream_service.entities.DefaultStreamInfo;
import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.exceptions.ApiException;
import com.stream.stream_service.repositories.StreamRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StreamService {
    
    @Autowired
    private StreamRepository streamRepository;
    
    @Autowired
    private DefaultStreamInfoService defaultStreamInfoService;
    

    @Transactional
    public Stream createStream(String channelId) {
        Optional<DefaultStreamInfo> defaultInfo = defaultStreamInfoService.getByChannelId(channelId);

        String title = defaultInfo.map(DefaultStreamInfo::getTitle).orElse("Untitled-Stream");
        String description = defaultInfo.map(DefaultStreamInfo::getDescription).orElse("No description available");

        Stream stream = new Stream();
        stream.setChannelId(channelId);
        stream.setTitle(title);
        stream.setDescription(description);
        stream.setIsLive(true);
        stream.setStartedAt(LocalDateTime.now());
        stream.setEndedAt(null);
        stream.setThumbnailUrl("GENERIC_THUMBNAIL_URL");
        stream.setViewers(0L);

        return streamRepository.save(stream);
    }
    
    public List<Stream> getStreamsByChannelId(String channelId) {
        return streamRepository.findByChannelId(channelId);
    }

    public Optional<Stream> getLiveStreamByChannelId(String channelId) {
        return streamRepository.findByChannelIdAndIsLiveTrue(channelId);
    }
    
    public List<Stream> getLiveStreams() {
        return streamRepository.findByIsLive(true);
    }
    
    @Transactional
    public Optional<Stream> updateStream(String channelId, String title, String description) {
        // Find live stream by channel ID
        Optional<Stream> optionalStream = streamRepository.findByChannelIdAndIsLiveTrue(channelId);

        if (optionalStream.isPresent()) {
            Stream stream = optionalStream.get();

            if (StringUtils.hasText(title)) {
                stream.setTitle(title);
            }

            if (description != null) {
                stream.setDescription(description);
            }

            streamRepository.save(stream);
            return Optional.of(stream);
        }

        return Optional.empty(); // No live stream found
    }
    
    @Transactional
    public Optional<Stream> incrementViewers(Long id) {
        return streamRepository.findById(id)
                .map(stream -> {
                    stream.setViewers(stream.getViewers() + 1);
                    return streamRepository.save(stream);
                });
    }

    @Transactional
    public Optional<Stream> decrementViewers(Long id) {
        return streamRepository.findById(id)
                .map(stream -> {
                    long currentViewers = stream.getViewers() != null ? stream.getViewers() : 0L;
                    if (currentViewers > 0) {
                        stream.setViewers(currentViewers - 1);
                    }
                    return streamRepository.save(stream);
                });
    }

    public Optional<Long> getViewersCount(Long id) {
        return streamRepository.findById(id)
                .map(Stream::getViewers);
        }
    public Stream endStream(Long id) {
        return streamRepository.findById(id)
                .map(stream -> {
                    stream.setIsLive(false);
                    stream.setEndedAt(LocalDateTime.now());
                    return streamRepository.save(stream);
                })
                .orElseThrow(() -> new ApiException("Stream not found", HttpStatus.NOT_FOUND));
    }
    
    public void deleteStream(Long id) {
        streamRepository.deleteById(id);
    }

}