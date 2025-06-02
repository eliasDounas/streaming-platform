package com.stream.stream_service.DTO;

import com.stream.stream_service.entities.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamWithChannelDto {
    private Stream stream;
    private ChannelDto channel;
}
