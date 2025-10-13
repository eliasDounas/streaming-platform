package com.channel.channel_service.handler;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.channel.channel_service.exceptions.ChannelNotFoundException;
import com.channel.channel_service.exceptions.ChatRoomNotFoundException;
import com.channel.channel_service.exceptions.UnauthorizedException;
import com.channel.channel_service.exceptions.UserAlreadyHasAChannelException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChannelNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleChannelNotFound(ChannelNotFoundException ex) {
        Map<String, Object> error = Map.of(
            "error", ex.getMessage(),
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleChatRoomNotFound(ChatRoomNotFoundException ex) {
        Map<String, Object> error = Map.of(
            "error", ex.getMessage(),
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> error = Map.of(
            "error", ex.getMessage(),
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.FORBIDDEN.value()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(UserAlreadyHasAChannelException.class)
    public ResponseEntity<Map<String, Object>> handleUserAlreadyHasChannel(UserAlreadyHasAChannelException ex) {
        Map<String, Object> error = Map.of(
            "error", ex.getMessage(),
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = Map.of(
            "error", ex.getMessage(),
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(java.io.IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(java.io.IOException ex) {
        System.err.println("IO error: " + ex.getMessage());
        Map<String, Object> error = Map.of(
            "error", "File operation failed",
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // Log the actual error for debugging
        System.err.println("Unexpected error: " + ex.getMessage());
        ex.printStackTrace();
        
        Map<String, Object> error = Map.of(
            "error", "An unexpected error occurred",
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
