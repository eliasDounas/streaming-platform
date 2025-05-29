package com.channel.channel_service.gRPC;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.channel.channel_service.DTO.ChannelPreviewDTO;
import com.channel.channel_service.services.ChannelService;
import com.example.grpc.ChannelIdList;
import com.example.grpc.ChannelPreviewList;
import com.example.grpc.ChannelPreview;
import com.example.grpc.ChannelServiceGrpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class ChannelServiceImpl extends ChannelServiceGrpc.ChannelServiceImplBase {

    @Autowired
    private ChannelService channelService;

    @Override
    public void getChannelPreviewsByIds(ChannelIdList request, StreamObserver<ChannelPreviewList> responseObserver) {
        List<String> ids = request.getIdsList();

        List<ChannelPreviewDTO> previews = channelService.getChannelPreviewsByIds(ids);

        ChannelPreviewList response = ChannelPreviewList.newBuilder()
            .addAllPreviews(
                previews.stream()
                    .map(preview -> ChannelPreview.newBuilder()
                        .setChannelId(preview.getChannelId())
                        .setName(preview.getName())
                        .setPlaybackUrl(preview.getPlaybackUrl())
                        .setAvatarUrl(preview.getAvatarUrl())
                        .build())
                    .collect(Collectors.toList())
            )
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
