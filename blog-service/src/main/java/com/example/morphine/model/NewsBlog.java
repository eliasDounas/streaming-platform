package com.example.morphine.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Document(collection = "news_blogs")
@Data
public class NewsBlog {
    @Id
    private String id;
    private String description;
    private String category;
    private String channelId;
    private String title;
    private String hook;
    private String content;
    private List<String> tags;
    private String coverImg;
    private Integer readingTime;
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getCreatedAtString() {
        return createdAt.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}