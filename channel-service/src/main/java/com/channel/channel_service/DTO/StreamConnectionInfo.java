package com.channel.channel_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StreamConnectionInfo {
    private String channelId;
    private String streamKey;
    private String ingestEndpoint;
}
