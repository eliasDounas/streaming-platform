package com.stream.stream_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.stream.stream_service.DTO.ChannelDto;
import com.stream.stream_service.entities.DefaultStreamInfo;
import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.enums.StreamCategory;
import com.stream.stream_service.exceptions.ApiException;
import com.stream.stream_service.gRPC.ChannelGrpcClient;
import com.stream.stream_service.repositories.StreamRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

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
        }       
        
        Optional<DefaultStreamInfo> defaultInfo = defaultStreamInfoService.findByChannelId(channel.getChannelId());
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

        Optional<DefaultStreamInfo> defaultInfo = defaultStreamInfoService.getStreamInfoWithUserId(userId);
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