package com.channel.channel_service.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    private String channelId;

    private String name;
    private String description;
    private boolean isLive;

    private String arn;
    private String streamKey;
    private String streamKeyArn;
    private String ingestEndpoint;
    private String playbackUrl;

    private String avatarUrl;
    private String userId;

    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "channel", cascade = CascadeType.ALL)
    private ChatRoom chatRoom;

}

