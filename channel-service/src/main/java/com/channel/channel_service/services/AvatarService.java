package com.channel.channel_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AvatarService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.avatar-bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    // Security constants
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );
    private static final int MAX_WIDTH = 2048;
    private static final int MAX_HEIGHT = 2048;

    public String uploadAvatar(MultipartFile file, String userId) throws IOException {
        // Security validations
        validateFile(file);
        
        // Validate actual image content
        byte[] fileBytes = file.getBytes();
        validateImageContent(fileBytes);
        
        // Generate secure file name (ignore original filename completely)
        String fileExtension = getSecureFileExtension(file.getContentType());
        String fileName = "avatars/" + sanitizeUserId(userId) + "/" + UUID.randomUUID() + fileExtension;        // Create the put request with secure content type
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(getSecureContentType(file.getContentType()))
                .build();

        // Upload the file
        s3Client.putObject(putObjectRequest, 
            RequestBody.fromBytes(fileBytes));

        // Generate the URL
        String avatarUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
            bucketName, region, fileName);

        // Print URL to console
        System.out.println("Avatar uploaded successfully!");
        System.out.println("Avatar URL: " + avatarUrl);

        return avatarUrl;
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + MAX_FILE_SIZE + " bytes");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, GIF, and WebP images are allowed");
        }

        // Check original filename extension (if provided)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = getFileExtension(originalFilename).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new IllegalArgumentException("Invalid file extension. Only .jpg, .jpeg, .png, .gif, .webp are allowed");
            }
        }
    }

    private void validateImageContent(byte[] fileBytes) throws IOException {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
            if (image == null) {
                throw new IllegalArgumentException("File is not a valid image");
            }

            // Check image dimensions
            if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
                throw new IllegalArgumentException("Image dimensions exceed maximum allowed size of " + MAX_WIDTH + "x" + MAX_HEIGHT);
            }

            // Ensure minimum dimensions (avoid 1x1 pixel attacks)
            if (image.getWidth() < 10 || image.getHeight() < 10) {
                throw new IllegalArgumentException("Image dimensions are too small. Minimum size is 10x10 pixels");
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid image file or corrupted content", e);
        }
    }

    private String getSecureFileExtension(String contentType) {
        if (contentType == null) {
            return ".jpg";
        }
        
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/webp":
                return ".webp";
            default:
                return ".jpg";
        }
    }

    private String getSecureContentType(String originalContentType) {
        if (originalContentType == null || !ALLOWED_CONTENT_TYPES.contains(originalContentType.toLowerCase())) {
            return "image/jpeg";
        }
        return originalContentType.toLowerCase();
    }    private String sanitizeUserId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        // Remove any potentially dangerous characters and limit length
        String sanitized = userId.replaceAll("[^a-zA-Z0-9-_]", "");
        
        // Check if sanitized string is empty
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("User ID contains no valid characters");
        }
        
        // Limit length to 50 characters using the sanitized string length
        return sanitized.substring(0, Math.min(sanitized.length(), 50));
    }private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
