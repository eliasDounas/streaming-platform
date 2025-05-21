package com.channel.channel_service.exceptions;

public class ChannelNotFoundException extends RuntimeException {
    public ChannelNotFoundException(String message) { super(message); }
}