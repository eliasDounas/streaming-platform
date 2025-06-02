package com.stream.stream_service.gRPC;

import com.example.grpc.ChannelServiceGrpc;
import com.example.grpc.UserIdRequest;
import com.stream.stream_service.DTO.ChannelDto;
import com.example.grpc.ChannelIdList;
import com.example.grpc.ChannelPreview;
import com.example.grpc.ChannelPreviewList;
import com.example.grpc.ChannelResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ChannelGrpcClient {

    @GrpcClient("channelService")  
    private ChannelServiceGrpc.ChannelServiceBlockingStub stub;

    public ChannelDto getChannelByUserId(long userId) {
        UserIdRequest request = UserIdRequest.newBuilder()
                .setUserId(userId)
                .build();

        ChannelResponse response = stub.getChannelByUserId(request);
        return mapToDto(response);
    }

    public List<ChannelDto> getChannelPreviewsByIds(List<String> ids) {
        ChannelIdList request = ChannelIdList.newBuilder()
                .addAllIds(ids)
                .build();

        ChannelPreviewList response = stub.getChannelPreviewsByIds(request);
        return response.getPreviewsList().stream()
                .map(this::mapToDto)
                .toList();
    }

    private ChannelDto mapToDto(ChannelResponse response) {
        return new ChannelDto(
                response.getChannelId(),
                response.getName(),
                response.getPlaybackUrl(),
                response.getAvatarUrl()
        );
    }

    private ChannelDto mapToDto(ChannelPreview preview) {
        return new ChannelDto(
                preview.getChannelId(),
                preview.getName(),
                preview.getPlaybackUrl(),
                preview.getAvatarUrl()
        );
    }
}
