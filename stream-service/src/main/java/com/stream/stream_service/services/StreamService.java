package com.stream.stream_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.stream.stream_service.DTO.ChannelDto;
import com.stream.stream_service.DTO.StreamWithChannelDto;
import com.stream.stream_service.DTO.PaginatedStreamResponse;
import com.stream.stream_service.entities.DefaultStreamInfo;
import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.enums.StreamCategory;
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
    public Stream createStream(String arn, String awsStreamId) {

        ChannelDto channel = channelGrpcClient.getChannelByArn(arn);
        if (channel == null) {
            throw new ApiException("Channel not found", HttpStatus.NOT_FOUND);
        }        Optional<DefaultStreamInfo> defaultInfo = defaultStreamInfoService.findByChannelId(channel.getChannelId());
        String title = defaultInfo.map(DefaultStreamInfo::getTitle).orElse("Untitled-Stream");
        String description = defaultInfo.map(DefaultStreamInfo::getDescription).orElse("No description available");
        StreamCategory category = defaultInfo.map(DefaultStreamInfo::getCategory).orElse(StreamCategory.OTHER);

        

        Stream stream = new Stream();
        stream.setChannelId(channel.getChannelId());
        stream.setAwsStreamId(awsStreamId);
        stream.setTitle(title);
        stream.setDescription(description);
        stream.setCategory(category);
        stream.setIsLive(true);
        stream.setStartedAt(LocalDateTime.now());
        stream.setEndedAt(null);
        stream.setViewers(0L);

        return streamRepository.save(stream);
    }

    /**
     * Create a stream using only channel ARN (legacy method - AWS stream ID will be null)
     * @param arn The channel ARN
     * @return Created stream
     */
    @Transactional
    public Stream createStream(String arn) {
        return createStream(arn, null);
    }

    /**
     * Get the live stream of a channel 
     * @param channelId The channel ID to get the live stream for
     * @return Optional StreamWithChannelDto containing the stream and channel info if found
     */
    public Optional<StreamWithChannelDto> getLiveStreamByChannelId(String channelId) {
        return streamRepository.findByChannelIdAndIsLiveTrue(channelId)
                .map(stream -> {
                    List<ChannelDto> channels = channelGrpcClient.getChannelPreviewsByIds(List.of(channelId));
                    ChannelDto channel = channels.isEmpty() ? null : channels.get(0);
                    return new StreamWithChannelDto(stream, channel);
                });
    }
    
    
    /**
     * Get all live streams with channel information
     * @return List of StreamWithChannelDto containing live streams and their associated channel info
     */
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
    public Optional<Stream> incrementViewers(String streamId) {
        return streamRepository.findById(streamId)
                .map(stream -> {
                    stream.setViewers(stream.getViewers() + 1);
                    return streamRepository.save(stream);
                });
    }

   
    public Optional<Long> getViewersCount(String streamId) {
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
    
    /**
     * End a specific stream using AWS stream ID
     * @param awsStreamId The AWS stream ID to identify the exact stream
     * @return Updated stream if found
     */
    @Transactional
    public Optional<Stream> endStreamByAwsStreamId(String awsStreamId) {
        System.out.println("=== DEBUG: endStreamByAwsStreamId called ===");
        System.out.println("AWS Stream ID: " + awsStreamId);
        
        Optional<Stream> streamOpt = streamRepository.findByAwsStreamId(awsStreamId);
        
        if (streamOpt.isPresent()) {
            Stream stream = streamOpt.get();
            System.out.println("Found stream ID: " + stream.getId() + 
                             ", channelId: " + stream.getChannelId() +
                             ", startedAt: " + stream.getStartedAt() + 
                             ", isLive: " + stream.getIsLive());
            
            stream.setIsLive(false);
            stream.setEndedAt(LocalDateTime.now());
            Stream saved = streamRepository.save(stream);
            System.out.println("Successfully ended stream ID: " + stream.getId());
            return Optional.of(saved);
        } else {
            System.err.println("No stream found with AWS stream ID: " + awsStreamId);
            return Optional.empty();
        }
    }
      public void deleteStream(String id, String userId) {

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

    /**
     * Get finished streams with pagination support (offset-based)
     * @param channelId The channel ID to get finished streams for
     * @param page Page number (0-based, e.g., 0 for first page, 1 for second page)
     * @param size Number of streams per page (e.g., 10)
     * @return List of finished streams for the specified page
     */

    public List<Stream> getFinishedStreamsByChannel(String channelId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return streamRepository.findByChannelIdAndIsLiveFalseOrderByStartedAtDesc(channelId, pageable);
    }

    /**
     * Get finished streams with full pagination metadata
     * @param channelId The channel ID to get finished streams for
     * @param page Page number (0-based)
     * @param size Number of streams per page
     * @return PaginatedStreamResponse with Stream entities and pagination metadata
     */
    public PaginatedStreamResponse<Stream> getFinishedStreamsWithMetadata(String channelId, int page, int size) {
        // Get streams for current page
        List<Stream> streams = getFinishedStreamsByChannel(channelId, page, size);
        
        // Get total count for pagination metadata
        long totalElements = streamRepository.countByChannelIdAndIsLiveFalse(channelId);
        int totalPages = (int) Math.ceil((double) totalElements / size);
          // Build pagination response
        return new PaginatedStreamResponse<>(
            streams,
            page,
            size,
            totalElements,
            totalPages
        );
    }
      /**
     * Get all finished streams with channel information and pagination metadata, sorted by view count
     * @param page Page number (0-based)
     * @param size Number of streams per page
     * @return PaginatedStreamResponse with StreamWithChannelDto and pagination metadata
     */
    public PaginatedStreamResponse<StreamWithChannelDto> getFinishedStreamsWithChannelInfo(int page, int size) {
        // Get streams for current page, sorted by views descending
        Pageable pageable = PageRequest.of(page, size);
        List<Stream> streams = streamRepository.findByIsLiveFalseOrderByViewersDesc(pageable);
        
        // Get total count for pagination metadata
        long totalElements = streamRepository.countByIsLiveFalse();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        // Get channel info and build StreamWithChannelDto list
        List<StreamWithChannelDto> content = List.of();
        if (!streams.isEmpty()) {
            // Get unique channel IDs
            List<String> channelIds = streams.stream()
                    .map(Stream::getChannelId)
                    .distinct()
                    .toList();
            
            // Fetch channel info for all channels
            List<ChannelDto> channelDtos = channelGrpcClient.getChannelPreviewsByIds(channelIds);
            
            // Map by channelId for easy lookup
            Map<String, ChannelDto> channelMap = channelDtos.stream()
                    .collect(Collectors.toMap(ChannelDto::getChannelId, Function.identity()));
            
            content = streams.stream()
                    .map(stream -> new StreamWithChannelDto(stream, channelMap.get(stream.getChannelId())))
                    .toList();
        }
        
        // Build pagination response
        return new PaginatedStreamResponse<>(
            content,
            page,
            size,
            totalElements,
            totalPages
        );
    }
    


    /**
     * Update thumbnail URL for a specific stream using AWS stream ID
     * @param awsStreamId The AWS stream ID to identify the exact stream
     * @param thumbnailUrl The thumbnail URL to set
     * @return Updated stream if found
     */
    @Transactional
    public Optional<Stream> updateStreamThumbnailByAwsStreamId(String awsStreamId, String thumbnailUrl) {
        System.out.println("=== DEBUG: updateStreamThumbnailByAwsStreamId called ===");
        System.out.println("AWS Stream ID: " + awsStreamId);
        System.out.println("Thumbnail URL: " + thumbnailUrl);
        
        Optional<Stream> streamOpt = streamRepository.findByAwsStreamId(awsStreamId);
        
        if (streamOpt.isPresent()) {
            Stream stream = streamOpt.get();
            System.out.println("Found stream ID: " + stream.getId() + 
                             ", channelId: " + stream.getChannelId() +
                             ", startedAt: " + stream.getStartedAt() + 
                             ", isLive: " + stream.getIsLive());
            
            stream.setThumbnailUrl(thumbnailUrl);
            Stream saved = streamRepository.save(stream);
            System.out.println("Successfully updated thumbnail for stream ID: " + stream.getId());
            return Optional.of(saved);
        } else {
            System.err.println("No stream found with AWS stream ID: " + awsStreamId);
            return Optional.empty();
        }
    }
    /**
     * Update VOD URL for a specific stream using AWS stream ID
     * @param awsStreamId The AWS stream ID to identify the exact stream
     * @param vodUrl The VOD URL to set
     * @return Updated stream if found
     */
    @Transactional
    public Optional<Stream> updateStreamVodUrlByAwsStreamId(String awsStreamId, String vodUrl) {
        System.out.println("=== DEBUG: updateStreamVodUrlByAwsStreamId called ===");
        System.out.println("AWS Stream ID: " + awsStreamId);
        System.out.println("VOD URL: " + vodUrl);
        
        Optional<Stream> streamOpt = streamRepository.findByAwsStreamId(awsStreamId);
        
        if (streamOpt.isPresent()) {
            Stream stream = streamOpt.get();
            System.out.println("Found stream ID: " + stream.getId() + 
                             ", channelId: " + stream.getChannelId() +
                             ", startedAt: " + stream.getStartedAt() + 
                             ", isLive: " + stream.getIsLive());
            
            stream.setVodUrl(vodUrl);
            Stream saved = streamRepository.save(stream);
            System.out.println("Successfully updated VOD URL for stream ID: " + stream.getId());
            return Optional.of(saved);
        } else {
            System.err.println("No stream found with AWS stream ID: " + awsStreamId);
            return Optional.empty();
        }
    }
}