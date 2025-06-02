package com.stream.stream_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultStreamInfoRequest {
    private String title;
    private String description;
}