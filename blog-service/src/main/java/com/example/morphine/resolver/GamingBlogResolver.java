package com.example.morphine.resolver;

import com.example.morphine.dto.ChannelDTO;
import com.example.morphine.dto.ChannelPreviewDTO;
import com.example.morphine.dto.GamingBlogOutputDTO;
import com.example.morphine.input.GamingBlogInput;
import com.example.morphine.model.GamingBlog;
import com.example.morphine.service.GamingBlogService;
import com.example.morphine.client.ChannelServiceClient;
import com.example.morphine.config.GraphQLConfig;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GamingBlogResolver {

    private final GamingBlogService service;
    private final ChannelServiceClient channelClient;

    public GamingBlogResolver(GamingBlogService service, ChannelServiceClient channelClient) {
        this.service = service;
        this.channelClient = channelClient;
    }    @MutationMapping
    public GamingBlogOutputDTO createGamingBlogs(@Argument("gaming") GamingBlogInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Gaming input cannot be null");
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

        GamingBlog blog = convert(input);
        blog.setChannelId(channel.getChannelId());
        GamingBlog created = service.create(blog);
        return new GamingBlogOutputDTO(created, channel);
    }    @MutationMapping
    public GamingBlogOutputDTO updateGamingBlog(@Argument String id, @Argument("gaming") GamingBlogInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Gaming input cannot be null");
        }

        // Extract userId from X-User-Id header
        String userId = GraphQLConfig.extractUserIdFromRequest();
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required in X-User-Id header");
        }

        // Verify the blog exists and belongs to the user
        GamingBlog existingBlog = service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("GamingBlog not found with id: " + id));

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
        GamingBlog updatedBlog = convert(input);
        updatedBlog.setChannelId(channel.getChannelId());
        GamingBlog saved = service.update(id, updatedBlog);
        
        if (saved == null) {
            throw new IllegalArgumentException("Failed to update blog with id: " + id);
        }
        
        return new GamingBlogOutputDTO(saved, channel);
    }

    @QueryMapping
    public List<GamingBlogOutputDTO> getGamingBlogs() {
        List<GamingBlog> blogs = service.getAll();
        if (blogs.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> channelIds = blogs.stream()
                .map(GamingBlog::getChannelId)
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
                .map(blog -> new GamingBlogOutputDTO(blog, channelMap.get(blog.getChannelId())))
                .collect(Collectors.toList());
    }

    @QueryMapping
    public GamingBlogOutputDTO getGamingBlogById(@Argument String id) {
        GamingBlog blog = service.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("GamingBlog not found with id: " + id));

        ChannelDTO channel = channelClient.getChannelByUserId(String.valueOf(Long.parseLong(blog.getChannelId())));
        return new GamingBlogOutputDTO(blog, channel);
    }

    @QueryMapping
    public List<GamingBlogOutputDTO> getGamingBlogsByCategory(@Argument String category) {
        List<GamingBlog> blogs = service.getByCategory(category);
        return enrichWithChannelPreviews(blogs);
    }

    @QueryMapping
    public List<GamingBlogOutputDTO> getGamingBlogsByTag(@Argument String tag) {
        List<GamingBlog> blogs = service.getByTag(tag);
        return enrichWithChannelPreviews(blogs);
    }

    @QueryMapping
    public List<GamingBlogOutputDTO> getGamingBlogsByChannel(@Argument String channelId) {
        List<GamingBlog> blogs = service.getByChannelId(channelId);
        return enrichWithChannelPreviews(blogs);
    }

    private List<GamingBlogOutputDTO> enrichWithChannelPreviews(List<GamingBlog> blogs) {
        if (blogs == null || blogs.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> channelIds = blogs.stream()
                .map(GamingBlog::getChannelId)
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
                .map(blog -> new GamingBlogOutputDTO(blog, channelMap.get(blog.getChannelId())))
                .collect(Collectors.toList());
    }

    private GamingBlog convert(GamingBlogInput input) {
        GamingBlog blog = new GamingBlog();
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
