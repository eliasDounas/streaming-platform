package com.stream.stream_service.DTO;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IvsEvent {
    private String source;
    private String account;
    private String region;
    private String detailType; // maps to "detail-type"
    private List<String> resources;
    private IvsDetail detail;
}