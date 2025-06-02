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
    public Stream createStream(long userId) {
        Optional<DefaultStreamInfo> defaultInfo = defaultStreamInfoService.getByChannelId(userId);

        String title = defaultInfo.map(DefaultStreamInfo::getTitle).orElse("Untitled-Stream");
        String description = defaultInfo.map(DefaultStreamInfo::getDescription).orElse("No description available");
        String channelId = defaultInfo.map(DefaultStreamInfo::getChannelId)
                .orElseThrow(() -> new ApiException("This user doesn't have a channel", HttpStatus.NOT_FOUND));

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
    public Optional<Stream> incrementViewers(Long streamId) {
        return streamRepository.findById(streamId)
                .map(stream -> {
                    stream.setViewers(stream.getViewers() + 1);
                    return streamRepository.save(stream);
                });
    }

    @Transactional
    public Optional<Stream> decrementViewers(Long streamId) {

        return streamRepository.findById(streamId)
                .map(stream -> {
                    long currentViewers = stream.getViewers() != null ? stream.getViewers() : 0L;
                    if (currentViewers > 0) {
                        stream.setViewers(currentViewers - 1);
                    }
                    return streamRepository.save(stream);
                });
    }

    public Optional<Long> getViewersCount(Long streamId) {
        return streamRepository.findById(streamId)
                .map(Stream::getViewers);
        }
    
    //Update this later to end stream after it receives AWS notification
    public Stream endStream(Long userId, Long id) {

        Optional<DefaultStreamInfo> defaultInfo = defaultStreamInfoService.getByChannelId(userId);
        String channelId = defaultInfo.map(DefaultStreamInfo::getChannelId)
                .orElseThrow(() -> new ApiException("This user doesn't have a channel", HttpStatus.NOT_FOUND));
        // Check if the stream belongs to the channel
        Stream stream = streamRepository.findById(id)
                .orElseThrow(() -> new ApiException("Stream not found", HttpStatus.NOT_FOUND));
        if (!stream.getChannelId().equals(channelId)) {
            throw new ApiException("This stream does not belong to the channel", HttpStatus.FORBIDDEN);
        }
        return streamRepository.findById(id)
                .map(str -> {
                    str.setIsLive(false);
                    str.setEndedAt(LocalDateTime.now());
                    return streamRepository.save(str);
                }).orElseThrow(() -> new ApiException("Stream not found", HttpStatus.NOT_FOUND));
    }
    
    public void deleteStream(Long id, Long userId) {

        Optional<DefaultStreamInfo> defaultInfo = defaultStreamInfoService.getByChannelId(userId);
        String channelId = defaultInfo.map(DefaultStreamInfo::getChannelId)
                .orElseThrow(() -> new ApiException("This user doesn't have a channel", HttpStatus.NOT_FOUND));
        // Check if the stream belongs to the channel
        Stream stream = streamRepository.findById(id)
                .orElseThrow(() -> new ApiException("Stream not found", HttpStatus.NOT_FOUND));
        if (!stream.getChannelId().equals(channelId)) {
            throw new ApiException("This stream does not belong to the channel", HttpStatus.FORBIDDEN);
        }
        
        // Delete the stream
        streamRepository.deleteById(id);
    }

}