package com.example.morphine.repository;

import com.example.morphine.model.GamingBlog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface GamingBlogRepository extends MongoRepository<GamingBlog, String> {
    List<GamingBlog> findByCategory(String category);

    // Ajoutez aussi ces méthodes si nécessaire
    List<GamingBlog> findByTagsContaining(String tag);
    List<GamingBlog> findByChannelId(String channelId);
}
