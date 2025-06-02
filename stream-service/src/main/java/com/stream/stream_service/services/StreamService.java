package com.stream.stream_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.stream.stream_service.DTO.ChannelDto;
import com.stream.stream_service.DTO.StreamWithChannelDto;
import com.stream.stream_service.entities.DefaultStreamInfo;
import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.exceptions.ApiException;
import com.stream.stream_service.gRPC.ChannelGrpcClient;
import com.stream.stream_service.repositories.StreamRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StreamService {
    
    @Autowired
    private StreamRepository streamRepository;
    
    @Autowired
    private DefaultStreamInfoService defaultStreamInfoService;
    

    @Autowired
    private ChannelGrpcClient channelGrpcClient;

    @Transactional
    public Stream createStream(String arn) {

        ChannelDto channel = channelGrpcClient.getChannelByArn(arn);
        if (channel == null) {
            throw new ApiException("Channel not found", HttpStatus.NOT_FOUND);
        }
        Optional<DefaultStreamInfo> defaultInfo = defaultStreamInfoService.findByChannelId(channel.getChannelId());
        
        String title = defaultInfo.map(DefaultStreamInfo::getTitle).orElse("Untitled-Stream");
        String description = defaultInfo.map(DefaultStreamInfo::getDescription).orElse("No description available");

        Stream stream = new Stream();
        stream.setChannelId(channel.getChannelId());
        stream.setTitle(title);
        stream.setDescription(description);
        stream.setIsLive(true);
        stream.setStartedAt(LocalDateTime.now());
        stream.setEndedAt(null);
        stream.setThumbnailUrl("GENERIC_THUMBNAIL_URL");
        stream.setViewers(0L);

        return streamRepository.save(stream);
    }

    public Optional<StreamWithChannelDto> getLiveStreamByChannelId(String channelId) {
        return streamRepository.findByChannelIdAndIsLiveTrue(channelId)
                .map(stream -> {
                    List<ChannelDto> channels = channelGrpcClient.getChannelPreviewsByIds(List.of(channelId));
                    ChannelDto channel = channels.isEmpty() ? null : channels.get(0);
                    return new StreamWithChannelDto(stream, channel);
                });
    }
    
    
    public List<StreamWithChannelDto> getLiveStreams() {
        List<Stream> streams = streamRepository.findByIsLive(true);
        List<String> channelIds = streams.stream()
                .map(Stream::getChannelId)
                .toList();
    
        List<ChannelDto> channelDtos = channelGrpcClient.getChannelPreviewsByIds(channelIds);
    
        // Map by channelId for easy lookup
        Map<String, ChannelDto> channelMap = channelDtos.stream()
                .collect(Collectors.toMap(ChannelDto::getChannelId, Function.identity()));
    
        return streams.stream()
                .map(stream -> new StreamWithChannelDto(
                        stream,
                        channelMap.get(stream.getChannelId()) // may be null if channel not found
                ))
                .toList();
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
    public Stream endStream(String arn) {

        // 1. Get channel info by ARN
        ChannelDto channel = channelGrpcClient.getChannelByArn(arn);
        if (channel == null || channel.getChannelId() == null) {
            throw new ApiException("Channel not found", HttpStatus.NOT_FOUND);
        }

        // 2. Get the live stream for this channel
        Stream stream = streamRepository.findByChannelIdAndIsLiveTrue(channel.getChannelId())
            .orElseThrow(() -> new ApiException("No live stream found for this channel", HttpStatus.NOT_FOUND));

        // 3. Update stream state
        stream.setIsLive(false);
        stream.setEndedAt(LocalDateTime.now());

        return streamRepository.save(stream);
    }
    
    public void deleteStream(Long id, Long userId) {

        Optional<DefaultStreamInfo> defaultInfo = defaultStreamInfoService.getChannelWithUserId(userId);
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