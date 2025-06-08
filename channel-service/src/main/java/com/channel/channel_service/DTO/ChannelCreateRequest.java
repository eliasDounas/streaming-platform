package com.channel.channel_service.DTO;

import lombok.Data;

@Data
public class ChannelCreateRequest {
    private String name;
    private String description;
    // avatarUrl removed - will be handled via file upload
}
