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

/**
 * Secure Avatar Upload Service
 * 
 * This service handles avatar image uploads to AWS S3 with comprehensive security measures
 * to prevent various attack vectors including:
 * - File size attacks (DoS via large files)
 * - MIME type confusion attacks (file diff from declared type kind of XSS)
 * - Directory traversal attacks
 * - Image bomb attacks (excessive dimensions) (file that appears small but once opened explodes in size)
 * - Content type spoofing (manipulate Content-Type header)
 * - File extension bypasses (shell.php.jpg)
 * 
 * Security Features Implemented:
 * 1. Multi-layered file validation (MIME type + extension + actual content)
 * 2. Secure file naming (ignores user-provided filenames)
 * 3. Content type normalization and sanitization
 * 4. Image content validation using ImageIO
 * 5. Dimension limits to prevent memory exhaustion
 * 6. User ID sanitization to prevent path traversal
 * 7. File size limits to prevent storage DoS
 */

@Service
public class AvatarService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.avatar-bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    // Security constants
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB max to prevent DoS
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );
    private static final int MAX_WIDTH = 2048;  // Prevent memory exhaustion
    private static final int MAX_HEIGHT = 2048;

    /**
     * Uploads avatar with multi-layer security validation: size limits, MIME/extension checks,
     * ImageIO content verification, secure filename generation, and user ID sanitization.
     * 
     * @param file Avatar image file
     * @param userId User ID for the avatar
     * @return Public URL of uploaded avatar
     * @throws IOException If file processing fails
     * @throws IllegalArgumentException If validation fails
     */
    public String uploadAvatar(MultipartFile file, String userId) throws IOException {
        // Layer 1: Basic file validation (size, MIME type, extension)
        validateFile(file);
        
        // Layer 2: Deep content validation - Reads actual image data to verify it's a real image
        // This prevents malicious files disguised as images
        byte[] fileBytes = file.getBytes();
        validateImageContent(fileBytes);
        
        // Security: Generate completely new filename ignoring user input
        // This prevents directory traversal and malicious filename attacks
        String fileExtension = getSecureFileExtension(file.getContentType());
        String fileName = "avatars/" + sanitizeUserId(userId) + "/" + UUID.randomUUID() + fileExtension;
        
        // Security: Normalize content type to prevent MIME confusion attacks
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(getSecureContentType(file.getContentType()))
                .build();

        // Upload using validated and sanitized data only
        s3Client.putObject(putObjectRequest, 
            RequestBody.fromBytes(fileBytes));

        // Generate the public URL for the uploaded avatar
        String avatarUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
            bucketName, region, fileName);

        // Log successful upload (consider using proper logging instead of System.out)
        System.out.println("Avatar uploaded successfully!");
        System.out.println("Avatar URL: " + avatarUrl);

        return avatarUrl;
    }

    /**
     * First validation layer: checks file existence, size limits, MIME type whitelist,
     * and file extensions to quickly reject malicious uploads.
     */
    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // File size check - prevent DoS
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + MAX_FILE_SIZE + " bytes");
        }

        // MIME type whitelist validation
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, GIF, and WebP images are allowed");
        }

        // Extension validation as secondary defense
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = getFileExtension(originalFilename).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new IllegalArgumentException("Invalid file extension. Only .jpg, .jpeg, .png, .gif, .webp are allowed");
            }
        }
    }

    /**
     * Deep validation: uses ImageIO to verify actual image content, prevent image bombs,
     * and block malicious files disguised as images.
     */
    private void validateImageContent(byte[] fileBytes) throws IOException {
        try {
            // Parse actual image content - fails for non-images
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
            if (image == null) {
                throw new IllegalArgumentException("File is not a valid image");
            }

            // Dimension checks - prevent memory attacks and tracking pixels
            if (image.getWidth() > MAX_WIDTH || image.getHeight() > MAX_HEIGHT) {
                throw new IllegalArgumentException("Image dimensions exceed maximum allowed size of " + MAX_WIDTH + "x" + MAX_HEIGHT);
            }

            if (image.getWidth() < 10 || image.getHeight() < 10) {
                throw new IllegalArgumentException("Image dimensions are too small. Minimum size is 10x10 pixels");
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid image file or corrupted content", e);
        }
    }

    /**
     * Maps validated MIME types to secure extensions, ignoring user-provided filenames.
     */
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

    /**
     * Normalizes content type for S3, prevents MIME confusion attacks.
     */
    private String getSecureContentType(String originalContentType) {
        if (originalContentType == null || !ALLOWED_CONTENT_TYPES.contains(originalContentType.toLowerCase())) {
            return "image/jpeg";
        }
        return originalContentType.toLowerCase();
    }    /**
     * Sanitizes user ID to prevent directory traversal and path injection attacks.
     * Uses whitelist approach: only alphanumeric, dash, underscore allowed.
     */
    private String sanitizeUserId(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        String sanitized = userId.replaceAll("[^a-zA-Z0-9-_]", "");
        
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("User ID contains no valid characters");
        }
        
        return sanitized.substring(0, Math.min(sanitized.length(), 50));
    }    /**
     * Safely extracts file extension from user filename (validation only).
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
