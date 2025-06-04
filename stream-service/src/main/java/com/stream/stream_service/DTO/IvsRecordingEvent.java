package com.stream.stream_service.DTO;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IvsRecordingEvent {
    private String region;
    private List<String> resources;
    private Detail detail;

    @Data
    public static class Detail {
        private String recording_status;
        private String recording_s3_bucket_name;
        private String recording_s3_key_prefix;
        private String channel_name;
        private String stream_id;
        private String recording_session_id;
    }
}
