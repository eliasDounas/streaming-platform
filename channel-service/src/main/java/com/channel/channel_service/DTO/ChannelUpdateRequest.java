package com.channel.channel_service.DTO;

import lombok.Data;

@Data
public class ChannelUpdateRequest {
    private String userId;
    private String name;
    private String description;
    private String avatarUrl;
}