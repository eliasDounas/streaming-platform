package com.example.morphine.service;

import com.example.morphine.model.GamingBlog;
import com.example.morphine.repository.GamingBlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GamingBlogService {
    @Autowired
    private GamingBlogRepository repo;

    public GamingBlog create(GamingBlog blog) { return repo.save(blog); }

    public List<GamingBlog> getAll() { return repo.findAll(); }

    public Optional<GamingBlog> getById(String id) { return repo.findById(id); }

    public List<GamingBlog> getByCategory(String category) {
        return repo.findByCategory(category);
    }

    public List<GamingBlog> getByTag(String tag) {
        return repo.findByTagsContaining(tag);
    }
    public List<GamingBlog> getByChannelId(String channelId) {
        return repo.findByChannelId(channelId);
    }

    public void delete(String id) { repo.deleteById(id); }

    public GamingBlog update(String id, GamingBlog newBlog) {
        return repo.findById(id).map(existing -> {
            newBlog.setId(id);
            return repo.save(newBlog);
        }).orElse(null);
    }
}

