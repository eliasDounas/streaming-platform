package com.example.morphine.resolver;

import com.example.morphine.dto.ChannelDTO;
import com.example.morphine.dto.ChannelPreviewDTO;
import com.example.morphine.dto.NewsBlogOutputDTO;
import com.example.morphine.input.NewsBlogInput;
import com.example.morphine.model.NewsBlog;
import com.example.morphine.service.NewsBlogService;
import com.example.morphine.client.ChannelServiceClient;
import com.example.morphine.config.GraphQLConfig;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class NewsBlogResolver {

    private final NewsBlogService service;
    private final ChannelServiceClient channelClient;

    public NewsBlogResolver(NewsBlogService service, ChannelServiceClient channelClient) {
        this.service = service;
        this.channelClient = channelClient;
    }    @MutationMapping
    public NewsBlogOutputDTO createNewsBlogs(@Argument("news") NewsBlogInput input) {
        if (input == null) {
            throw new IllegalArgumentException("News input cannot be null");
        }

        // Extract userId from X-User-Id header
        String userId = GraphQLConfig.extractUserIdFromRequest();
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required in X-User-Id header");
        }

        ChannelDTO channel = channelClient.getChannelByUserId(userId);
        if (channel == null) {
            throw new IllegalArgumentException("No channel found for userId: " + userId);
        }

        NewsBlog blog = convert(input);
        blog.setChannelId(channel.getChannelId());
        NewsBlog created = service.create(blog);
        return new NewsBlogOutputDTO(created, channel);
    }    @MutationMapping
    public NewsBlogOutputDTO updateNewsBlog(@Argument String id, @Argument("news") NewsBlogInput input) {
        if (input == null) {
            throw new IllegalArgumentException("News input cannot be null");
        }

        // Extract userId from X-User-Id header
        String userId = GraphQLConfig.extractUserIdFromRequest();
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required in X-User-Id header");
        }

        // Verify the blog exists and belongs to the user
        NewsBlog existingBlog = service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("NewsBlog not found with id: " + id));

        // Get the user's channel to verify ownership
        ChannelDTO channel = channelClient.getChannelByUserId(userId);
        if (channel == null) {
            throw new IllegalArgumentException("No channel found for userId: " + userId);
        }

        // Verify the blog belongs to this user's channel
        if (!channel.getChannelId().equals(existingBlog.getChannelId())) {
            throw new IllegalArgumentException("You can only update your own blog posts");
        }

        // Update the blog
        NewsBlog updatedBlog = convert(input);
        updatedBlog.setChannelId(channel.getChannelId());
        NewsBlog saved = service.update(id, updatedBlog);
        
        if (saved == null) {
            throw new IllegalArgumentException("Failed to update blog with id: " + id);
        }
        
        return new NewsBlogOutputDTO(saved, channel);
    }

    @QueryMapping
    public List<NewsBlogOutputDTO> getNewsBlogs() {
        List<NewsBlog> blogs = service.getAll();
        if (blogs.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> channelIds = blogs.stream()
                .map(NewsBlog::getChannelId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<ChannelPreviewDTO> previews = channelClient.getChannelPreviewsByIds(channelIds);

        Map<String, ChannelDTO> channelMap = previews.stream()
                .collect(Collectors.toMap(
                        ChannelPreviewDTO::getChannelId,
                        preview -> new ChannelDTO(
                                preview.getChannelId(),
                                preview.getName(),
                                preview.getPlaybackUrl(),
                                preview.getAvatarUrl()
                        )));

        return blogs.stream()
                .map(blog -> new NewsBlogOutputDTO(blog, channelMap.get(blog.getChannelId())))
                .collect(Collectors.toList());
    }

    @QueryMapping
    public NewsBlogOutputDTO getNewsById(@Argument String id) {
        NewsBlog blog = service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("NewsBlog not found with id: " + id));

        ChannelDTO channel = channelClient.getChannelByUserId(blog.getChannelId());
        return new NewsBlogOutputDTO(blog, channel);
    }

    @QueryMapping
    public List<NewsBlogOutputDTO> getNewsBlogsByCategory(@Argument String category) {
        List<NewsBlog> blogs = service.getByCategory(category);
        return enrichWithChannelPreviews(blogs);
    }

    @QueryMapping
    public List<NewsBlogOutputDTO> getNewsBlogsByTag(@Argument String tag) {
        List<NewsBlog> blogs = service.getByTag(tag);
        return enrichWithChannelPreviews(blogs);
    }

    @QueryMapping
    public List<NewsBlogOutputDTO> getNewsBlogsByChannel(@Argument String channelId) {
        List<NewsBlog> blogs = service.getByChannelId(channelId);
        return enrichWithChannelPreviews(blogs);
    }

    private List<NewsBlogOutputDTO> enrichWithChannelPreviews(List<NewsBlog> blogs) {
        if (blogs == null || blogs.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> channelIds = blogs.stream()
                .map(NewsBlog::getChannelId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<ChannelPreviewDTO> previews = channelClient.getChannelPreviewsByIds(channelIds);

        Map<String, ChannelDTO> channelMap = previews.stream()
                .collect(Collectors.toMap(
                        ChannelPreviewDTO::getChannelId,
                        preview -> new ChannelDTO(
                                preview.getChannelId(),
                                preview.getName(),
                                preview.getPlaybackUrl(),
                                preview.getAvatarUrl()
                        )));

        return blogs.stream()
                .map(blog -> new NewsBlogOutputDTO(blog, channelMap.get(blog.getChannelId())))
                .collect(Collectors.toList());
    }

    private NewsBlog convert(NewsBlogInput input) {
        NewsBlog blog = new NewsBlog();
        blog.setTitle(input.getTitle());
        blog.setHook(input.getHook());
        blog.setContent(input.getContent());
        blog.setDescription(input.getDescription());
        blog.setCategory(input.getCategory());
        blog.setTags(input.getTags());
        blog.setCoverImg(input.getCoverImg());
        blog.setReadingTime(input.getReadingTime());
        return blog;
    }
}
