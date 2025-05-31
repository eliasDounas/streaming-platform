package com.stream.stream_service.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "vods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vod {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Channel ID cannot be null")
    @Column(name = "channel_id", nullable = false)
    private String channelId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stream_id")
    private Stream stream;
    
    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "vod_url")
    private String vodUrl;
    
    @Column(name = "views")
    private Long views = 0L;
    @Column(name = "duration")
    private Long duration; // in seconds
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}