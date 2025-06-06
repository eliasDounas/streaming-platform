package com.example.morphine.client;

import com.example.grpc.*;
import com.example.morphine.dto.ChannelDTO;
import com.example.morphine.dto.ChannelPreviewDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper Spring pour appeler le service gRPC ChannelService.
 */
@Service
public class ChannelServiceClient {

    private final ChannelServiceGrpc.ChannelServiceBlockingStub stub;

    public ChannelServiceClient(ChannelServiceGrpc.ChannelServiceBlockingStub stub) {
        this.stub = stub;
    }

    public ChannelDTO getChannelByUserId(String userId) {
        UserIdRequest request = UserIdRequest.newBuilder()
                .setUserId(userId)
                .build();

        ChannelResponse response = stub.getChannelByUserId(request);
        return new ChannelDTO(
                response.getChannelId(),
                response.getName(),
                response.getPlaybackUrl(),
                response.getAvatarUrl()
        );
    }

    public List<ChannelPreviewDTO> getChannelPreviewsByIds(List<String> channelIds) {
        ChannelIdList request = ChannelIdList.newBuilder()
                .addAllIds(channelIds)
                .build();

        ChannelPreviewList response = stub.getChannelPreviewsByIds(request);
        return response.getPreviewsList()
                .stream()
                .map(preview -> new ChannelPreviewDTO(
                        preview.getChannelId(),
                        preview.getName(),
                        preview.getPlaybackUrl(),
                        preview.getAvatarUrl()
                ))
                .collect(Collectors.toList());
    }
}
