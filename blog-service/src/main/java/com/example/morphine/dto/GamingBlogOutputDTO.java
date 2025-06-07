package com.example.morphine.dto;

import com.example.morphine.model.GamingBlog;

public class GamingBlogOutputDTO {
    private GamingBlog gamingBlog;
    private ChannelDTO channel;

    public GamingBlogOutputDTO() { }

    public GamingBlogOutputDTO(GamingBlog gamingBlog, ChannelDTO channel) {
        this.gamingBlog = gamingBlog;
        this.channel = channel;
    }

    public GamingBlog getGamingBlog() {
        return gamingBlog;
    }

    public void setGamingBlog(GamingBlog gamingBlog) {
        this.gamingBlog = gamingBlog;
    }

    public ChannelDTO getChannel() {
        return channel;
    }

    public void setChannel(ChannelDTO channel) {
        this.channel = channel;
    }
}
