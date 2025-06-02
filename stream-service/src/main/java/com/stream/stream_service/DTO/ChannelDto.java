package com.stream.stream_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelDto {
    private String channelId;
    private String name;
    private String playbackUrl;
    private String avatarUrl;
}