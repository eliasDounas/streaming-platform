package com.channel.channel_service.exceptions;

public class UserAlreadyHasAChannelException extends RuntimeException {
    public UserAlreadyHasAChannelException(String message) { super(message); }
}