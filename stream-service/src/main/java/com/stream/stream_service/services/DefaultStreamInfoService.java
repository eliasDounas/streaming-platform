package com.stream.stream_service.services;

import com.stream.stream_service.entities.DefaultStreamInfo;
import com.stream.stream_service.exceptions.ApiException;
import com.stream.stream_service.repositories.DefaultStreamInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultStreamInfoService {

    @Autowired
    private DefaultStreamInfoRepository defaultStreamInfoRepository;

    @Autowired
    private StreamService streamService;

    public DefaultStreamInfo create(DefaultStreamInfo info) {
        if (defaultStreamInfoRepository.existsById(info.getChannelId())) {
            throw new ApiException("Channel already exists", HttpStatus.CONFLICT);
        }
        return defaultStreamInfoRepository.save(info);
    }

    public Optional<DefaultStreamInfo> getByChannelId(String channelId) {
        return defaultStreamInfoRepository.findById(channelId);
    }

       public DefaultStreamInfo update(String channelId, DefaultStreamInfo updatedInfo) {
        return defaultStreamInfoRepository.findById(channelId)
                .map(info -> {
                    // Update DefaultStreamInfo
                    info.setTitle(updatedInfo.getTitle());
                    info.setDescription(updatedInfo.getDescription());
                    DefaultStreamInfo savedInfo = defaultStreamInfoRepository.save(info);

                    // Update live Stream if it exists
                    streamService.updateStream(
                        channelId,
                        updatedInfo.getTitle(),
                        updatedInfo.getDescription()
                    );

                    return savedInfo;
                })
                .orElseThrow(() -> new ApiException("Channel not found", HttpStatus.NOT_FOUND));
    }

    public void delete(String channelId) {
        defaultStreamInfoRepository.deleteById(channelId);
    }
}
