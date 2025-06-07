package com.channel.channel_service.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicChannelInfo {
    private String channelId;
    private String name;
    private String description;
    private boolean isLive;
    private String playbackUrl;
    private String avatarUrl;
    private LocalDateTime createdAt;
}
