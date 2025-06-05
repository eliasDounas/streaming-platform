package com.stream.stream_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.stream.stream_service.enums.StreamCategory;



@Entity
@Table(name = "streams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stream {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Channel ID cannot be null")
    @Column(name = "channel_id", nullable = false)
    private String channelId;
    
    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "is_live", nullable = false)
    private Boolean isLive = false;
    
    @Column(name = "viewers")
    private Long viewers = 0L;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
      @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    @NotNull(message = "Category cannot be null")
    private StreamCategory category = StreamCategory.OTHER;
    
    @Column(name = "vod_url")
    private String vodUrl;
}