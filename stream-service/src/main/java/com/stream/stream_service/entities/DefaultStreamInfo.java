package com.stream.stream_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "default_stream_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultStreamInfo {

    @Id
    @NotNull(message = "Channel ID cannot be null")
    @Column(name = "channel_id", nullable = false, unique = true)
    private String channelId;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;


}
