package com.channel.channel_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IvsDetail {
    private String event_name;
    private String channel_name;
    private String stream_id;
}