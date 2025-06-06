package com.example.morphine.service;

import com.example.morphine.model.NewsBlog;
import com.example.morphine.repository.NewsBlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsBlogService {
    @Autowired
    private NewsBlogRepository repo;

    public NewsBlog create(NewsBlog blog) { return repo.save(blog); }

    public List<NewsBlog> getAll() { return repo.findAll(); }


    public Optional<NewsBlog> getById(String id) { return repo.findById(id); }

    public List<NewsBlog> getByTag(String tag) {
        return repo.findByTagsContaining(tag);
    }

    public List<NewsBlog> getByChannelId(String channelId) {
        return repo.findByChannelId(channelId);
    }

    public List<NewsBlog> getByCategory(String category) { // Nouvelle méthode
        return repo.findByCategory(category);
    }

    public boolean delete(String id) {
        if (!repo.existsById(id)) {
            return false; // Le document n'existe pas
        }

        try {
            repo.deleteById(id);
            return true; // Suppression réussie
        } catch (Exception e) {
            return false; // Échec de la suppression
        }
    }


    public NewsBlog update(String id, NewsBlog newBlog) {
        return repo.findById(id).map(existing -> {
            newBlog.setId(id);
            return repo.save(newBlog);
        }).orElse(null);
    }
}

