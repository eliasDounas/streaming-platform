package com.stream.stream_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.stream.stream_service.DTO.ChannelDto;
import com.stream.stream_service.DTO.StreamWithChannelDto;
import com.stream.stream_service.DTO.PaginatedStreamResponse;
import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.gRPC.ChannelGrpcClient;
import com.stream.stream_service.repositories.StreamRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service responsible for querying and retrieving stream data
 * Handles all read operations for streams including:
 * - Fetching live streams
 * - Fetching finished streams (VODs)
 * - Pagination support
 * - Channel information enrichment
 */
@Service
public class StreamQueryService {

    @Autowired
    private StreamRepository streamRepository;

    @Autowired
    private ChannelGrpcClient channelGrpcClient;

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
        
        // Return empty list immediately if no live streams
        if (streams.isEmpty()) {
            return List.of();
        }
        
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
     * Get a stream by ID with channel information
     * @param streamId The stream ID to get
     * @return Optional StreamWithChannelDto containing the stream and channel info if found
     */
    public Optional<StreamWithChannelDto> getStreamWithChannelById(String streamId) {
        return streamRepository.findById(streamId)
                .map(stream -> {
                    List<ChannelDto> channels = channelGrpcClient.getChannelPreviewsByIds(List.of(stream.getChannelId()));
                    ChannelDto channel = channels.isEmpty() ? null : channels.get(0);
                    return new StreamWithChannelDto(stream, channel);
                });
    }
}
