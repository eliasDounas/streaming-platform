package com.channel.channel_service.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IvsEvent {
    @JsonProperty("detail")
    private Detail detail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detail {
        @JsonProperty("channel_arn")
        private String channelArn;

        @JsonProperty("stream_state")
        private String streamState;

    }
}