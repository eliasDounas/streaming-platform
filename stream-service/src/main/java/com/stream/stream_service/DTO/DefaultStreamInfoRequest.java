package com.stream.stream_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.stream.stream_service.enums.StreamCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultStreamInfoRequest {
    private String title;
    private String description;
    private StreamCategory category;
}