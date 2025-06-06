package com.example.morphine.resolver;

import com.example.morphine.dto.ChannelDTO;
import com.example.morphine.dto.ChannelPreviewDTO;
import com.example.morphine.dto.GamingBlogOutputDTO;
import com.example.morphine.input.GamingBlogInput;
import com.example.morphine.model.GamingBlog;
import com.example.morphine.service.GamingBlogService;
import com.example.morphine.client.ChannelServiceClient;
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
    }

    @MutationMapping
    public GamingBlogOutputDTO createGamingBlogs(@Argument("gaming") GamingBlogInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Gaming input cannot be null");
        }

        String userId;
        try {
            userId = input.getUserId();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid userId: " + input.getUserId());
        }

        ChannelDTO channel = channelClient.getChannelByUserId(userId);
        if (channel == null) {
            throw new IllegalArgumentException("No channel found for userId: " + userId);
        }

        GamingBlog blog = convert(input);
        blog.setChannelId(channel.getChannelId());
        GamingBlog created = service.create(blog);
        return new GamingBlogOutputDTO(created, channel);
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
