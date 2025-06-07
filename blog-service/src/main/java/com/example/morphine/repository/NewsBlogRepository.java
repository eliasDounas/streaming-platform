package com.example.morphine.repository;

import com.example.morphine.model.NewsBlog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsBlogRepository extends MongoRepository<NewsBlog, String> {
    List<NewsBlog> findByTagsContaining(String tag);
    List<NewsBlog> findByChannelId(String channelId);
    List<NewsBlog> findByCategory(String category); // Ajouté
}

