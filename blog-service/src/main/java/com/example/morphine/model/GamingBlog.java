package com.example.morphine.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Document(collection = "gaming_blogs")
@Data
public class GamingBlog {
    @Id
    private String id;
    private String description;
    private String category;
    private String channelId;
    private String userId;
    private String title;
    private String hook;
    private String content;
    private List<String> tags;
    private String coverImg;
    private Integer readingTime;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Méthode pour obtenir createdAt au format String compatible GraphQL
    public String getCreatedAtString() {
        return createdAt.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}