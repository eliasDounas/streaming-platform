package com.example.morphine.dto;

public class ChannelPreviewDTO {
    private String channelId;
    private String name;
    private String playbackUrl;
    private String avatarUrl;

    public ChannelPreviewDTO() { }

    public ChannelPreviewDTO(String channelId, String name, String playbackUrl, String avatarUrl) {
        this.channelId = channelId;
        this.name = name;
        this.playbackUrl = playbackUrl;
        this.avatarUrl = avatarUrl;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaybackUrl() {
        return playbackUrl;
    }

    public void setPlaybackUrl(String playbackUrl) {
        this.playbackUrl = playbackUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
