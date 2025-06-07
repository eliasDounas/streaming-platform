package com.example.morphine.dto;

import com.example.morphine.model.NewsBlog;

public class NewsBlogOutputDTO {
    private NewsBlog newsBlog;
    private ChannelDTO channel;

    public NewsBlogOutputDTO() { }

    public NewsBlogOutputDTO(NewsBlog newsBlog, ChannelDTO channel) {
        this.newsBlog = newsBlog;
        this.channel = channel;
    }

    public NewsBlog getNewsBlog() {
        return newsBlog;
    }

    public void setNewsBlog(NewsBlog newsBlog) {
        this.newsBlog = newsBlog;
    }

    public ChannelDTO getChannel() {
        return channel;
    }

    public void setChannel(ChannelDTO channel) {
        this.channel = channel;
    }
}
