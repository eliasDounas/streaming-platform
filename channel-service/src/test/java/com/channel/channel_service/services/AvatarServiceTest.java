package com.channel.channel_service.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Simple tests for AvatarService - focusing on validation logic
 */
@ExtendWith(MockitoExtension.class)
class AvatarServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private AvatarService avatarService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(avatarService, "bucketName", "test-bucket");
        ReflectionTestUtils.setField(avatarService, "region", "us-east-1");
        
        // Mock S3 to always succeed (we're not testing AWS)
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().build());
    }

    // ========== VALID UPLOADS ==========

    @Test
    void uploadAvatar_ValidJpeg_ShouldSucceed() throws IOException {
        byte[] imageBytes = createValidImage(500, 500, "jpg");
        MockMultipartFile file = new MockMultipartFile("avatar", "test.jpg", "image/jpeg", imageBytes);

        String result = avatarService.uploadAvatar(file, "user123");

        assertNotNull(result);
        assertTrue(result.contains("avatars/"));
    }

    @Test
    void uploadAvatar_ValidPng_ShouldSucceed() throws IOException {
        byte[] imageBytes = createValidImage(500, 500, "png");
        MockMultipartFile file = new MockMultipartFile("avatar", "test.png", "image/png", imageBytes);

        String result = avatarService.uploadAvatar(file, "user123");

        assertNotNull(result);
        assertTrue(result.endsWith(".png"));
    }

    // ========== FILE SIZE VALIDATION ==========

    @Test
    void uploadAvatar_EmptyFile_ShouldThrowException() {
        MockMultipartFile file = new MockMultipartFile("avatar", "empty.jpg", "image/jpeg", new byte[0]);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> avatarService.uploadAvatar(file, "user123"));

        assertEquals("File cannot be empty", exception.getMessage());
    }

    @Test
    void uploadAvatar_FileTooLarge_ShouldThrowException() {
        byte[] largeFile = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile file = new MockMultipartFile("avatar", "large.jpg", "image/jpeg", largeFile);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> avatarService.uploadAvatar(file, "user123"));

        assertTrue(exception.getMessage().contains("File size exceeds maximum"));
    }

    // ========== MIME TYPE VALIDATION ==========

    @Test
    void uploadAvatar_InvalidMimeType_ShouldThrowException() throws IOException {
        byte[] imageBytes = createValidImage(500, 500, "jpg");
        MockMultipartFile file = new MockMultipartFile("avatar", "test.pdf", "application/pdf", imageBytes);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> avatarService.uploadAvatar(file, "user123"));

        assertTrue(exception.getMessage().contains("Invalid file type"));
    }

    // ========== FILE EXTENSION VALIDATION ==========

    @Test
    void uploadAvatar_InvalidExtension_ShouldThrowException() throws IOException {
        byte[] imageBytes = createValidImage(500, 500, "jpg");
        MockMultipartFile file = new MockMultipartFile("avatar", "test.php", "image/jpeg", imageBytes);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> avatarService.uploadAvatar(file, "user123"));

        assertTrue(exception.getMessage().contains("Invalid file extension"));
    }

    // ========== IMAGE CONTENT VALIDATION ==========

    @Test
    void uploadAvatar_NotAnImage_ShouldThrowException() {
        MockMultipartFile file = new MockMultipartFile("avatar", "fake.jpg", "image/jpeg", 
            "This is not an image!".getBytes());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> avatarService.uploadAvatar(file, "user123"));

        assertTrue(exception.getMessage().contains("not a valid image"));
    }

    // ========== DIMENSION VALIDATION ==========

    @Test
    void uploadAvatar_ImageTooSmall_ShouldThrowException() throws IOException {
        byte[] imageBytes = createValidImage(5, 5, "jpg");
        MockMultipartFile file = new MockMultipartFile("avatar", "tiny.jpg", "image/jpeg", imageBytes);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> avatarService.uploadAvatar(file, "user123"));

        assertTrue(exception.getMessage().contains("too small"));
    }

    @Test
    void uploadAvatar_ImageTooBig_ShouldThrowException() throws IOException {
        byte[] imageBytes = createValidImage(3000, 3000, "jpg");
        MockMultipartFile file = new MockMultipartFile("avatar", "huge.jpg", "image/jpeg", imageBytes);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> avatarService.uploadAvatar(file, "user123"));

        assertTrue(exception.getMessage().contains("exceed maximum"));
    }


    // ========== HELPER METHOD ==========

    private byte[] createValidImage(int width, int height, String format) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, width, height);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return baos.toByteArray();
    }
}
