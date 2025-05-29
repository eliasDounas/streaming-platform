package com.channel.channel_service.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelPreviewDTO {
    private String channelId;
    private String name;
    private String playbackUrl;
    private String avatarUrl; 
}
