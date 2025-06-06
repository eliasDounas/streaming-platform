package com.example.morphine.input;

import lombok.Data;
import java.util.List;

@Data
public class GamingBlogInput {
    private String description;
    private String category;
    private String userId;
    private String title;
    private String hook;
    private String content;
    private List<String> tags;
    private String coverImg;
    private Integer readingTime;
}
