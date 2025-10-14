package com.stream.stream_service.services;

import com.stream.stream_service.DTO.ChannelDto;
import com.stream.stream_service.entities.DefaultStreamInfo;
import com.stream.stream_service.enums.StreamCategory;
import com.stream.stream_service.exceptions.ApiException;
import com.stream.stream_service.gRPC.ChannelGrpcClient;
import com.stream.stream_service.repositories.DefaultStreamInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultStreamInfoService {

    @Autowired
    private DefaultStreamInfoRepository defaultStreamInfoRepository;

    @Autowired
    @Lazy
    private StreamService streamService;

    @Autowired
    private ChannelGrpcClient channelGrpcClient;
    
    
    public Optional<DefaultStreamInfo> findByChannelId(String channelId) {
        return defaultStreamInfoRepository.findByChannelId(channelId);
    }
    
    public Optional<DefaultStreamInfo> getStreamInfoWithUserId(String userId) {
        // Step 1: Get the channel from gRPC
        ChannelDto channel;
        try {
            channel = channelGrpcClient.getChannelByUserId(userId);
        } catch (Exception e) {
            throw new ApiException("This user doesn't have a channel", HttpStatus.NOT_FOUND);
        }

        String channelId = channel.getChannelId();
        
        // Step 2: Fetch DefaultStreamInfo by channelId
        return defaultStreamInfoRepository.findById(channelId);
    }

    public DefaultStreamInfo upsert(String userId, String title, String description, StreamCategory category) {
        // Step 1: Get the channel from gRPC
        ChannelDto channel;
        try {
            channel = channelGrpcClient.getChannelByUserId(userId);
        } catch (Exception e) {
            throw new ApiException("This user doesn't have a channel", HttpStatus.NOT_FOUND);
        }

        String channelId = channel.getChannelId();

        // Step 2: Check if the DefaultStreamInfo exists
        Optional<DefaultStreamInfo> optionalInfo = defaultStreamInfoRepository.findById(channelId);

        DefaultStreamInfo info = optionalInfo.orElseGet(() -> {
            // New entry if it doesn't exist
            DefaultStreamInfo newInfo = new DefaultStreamInfo();
            newInfo.setChannelId(channelId);
            newInfo.setTitle(title);
            newInfo.setDescription(description);
            if (category != null) {
                newInfo.setCategory(category);
            }
            return newInfo;
        });

        // Step 3: Set/update the title, description, and category
        info.setTitle(title);
        info.setDescription(description);
        if (category != null) {
            info.setCategory(category);
        }

        // Step 4: Save to DB
        DefaultStreamInfo saved = defaultStreamInfoRepository.save(info);

        // Step 5: Also update live latest stream 
        streamService.updateStream(channelId, title, description);

        return saved;
    }
       
    public void delete(String userId) {
        // Step 1: Get the channel from gRPC
        ChannelDto channel;
        try {
            channel = channelGrpcClient.getChannelByUserId(userId);
        } catch (Exception e) {
            throw new ApiException("This user doesn't have a channel", HttpStatus.NOT_FOUND);
        }

        String channelId = channel.getChannelId();
        
        // Step 2: Delete the DefaultStreamInfo by channelId
        defaultStreamInfoRepository.deleteById(channelId);
    }
}
