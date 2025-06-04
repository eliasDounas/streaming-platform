package com.stream.stream_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedStreamResponse<T> {
    private List<T> content;          // The actual data (streams)
    private int page;                 // Current page number (0-based)
    private int size;                 // Number of items per page
    private long totalElements;       // Total number of items across all pages
    private int totalPages;           // Total number of pages
}
