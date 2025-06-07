package com.example.morphine.resolver;

import com.example.morphine.dto.ChannelDTO;
import com.example.morphine.dto.ChannelPreviewDTO;
import com.example.morphine.dto.NewsBlogOutputDTO;
import com.example.morphine.input.NewsBlogInput;
import com.example.morphine.model.NewsBlog;
import com.example.morphine.service.NewsBlogService;
import com.example.morphine.client.ChannelServiceClient;
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
    }

    @MutationMapping
    public NewsBlogOutputDTO createNewsBlogs(@Argument("news") NewsBlogInput input) {
        if (input == null) {
            throw new IllegalArgumentException("News input cannot be null");
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

        NewsBlog blog = convert(input);
        blog.setChannelId(channel.getChannelId());
        NewsBlog created = service.create(blog);
        return new NewsBlogOutputDTO(created, channel);
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
