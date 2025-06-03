package com.channel.channel_service.gRPC;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.channel.channel_service.DTO.ChannelPreviewDTO;
import com.channel.channel_service.entities.Channel;
import com.channel.channel_service.services.ChannelService;
import com.example.grpc.ArnRequest;
import com.example.grpc.ChannelIdList;
import com.example.grpc.ChannelPreviewList;
import com.example.grpc.ChannelResponse;
import com.example.grpc.ChannelPreview;
import com.example.grpc.ChannelServiceGrpc;
import com.example.grpc.UserIdRequest;
import io.grpc.Status;

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

    public void getChannelByUserId(UserIdRequest request, StreamObserver<ChannelResponse> responseObserver) {
        String userId = request.getUserId();

        Optional<Channel> optionalChannel = channelService.getChannelByUserId(userId);

        if (optionalChannel.isPresent()) {
            Channel channel = optionalChannel.get();
            ChannelResponse response = ChannelResponse.newBuilder()
                    .setChannelId(channel.getChannelId())
                    .setName(channel.getName())
                    .setPlaybackUrl(channel.getPlaybackUrl())
                    .setAvatarUrl(channel.getAvatarUrl())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("This user does not have a channel.")
                    .asRuntimeException()
            );
        }
    }

    @Override
    public void getChannelByArn(ArnRequest request, StreamObserver<ChannelResponse> responseObserver) {
        String arn = request.getArn();
    
        Optional<Channel> optionalChannel = channelService.getChannelByArn(arn); // assumes this method exists
    
        if (optionalChannel.isPresent()) {
            Channel channel = optionalChannel.get();
            ChannelResponse response = ChannelResponse.newBuilder()
                    .setChannelId(channel.getChannelId())
                    .setName(channel.getName())
                    .setPlaybackUrl(channel.getPlaybackUrl())
                    .setAvatarUrl(channel.getAvatarUrl())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription("Channel with given ARN not found.")
                    .asRuntimeException()
            );
        }
    }
}