package com.channel.channel_service.DTO;

import lombok.Data;

@Data
public class ChannelUpdateRequest {
    private Long userId;
    private String name;
    private String description;
    private String avatarUrl;
}