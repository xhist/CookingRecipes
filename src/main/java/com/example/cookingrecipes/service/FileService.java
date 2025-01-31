package com.example.cookingrecipes.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    private final Path uploadPath;

    public FileService(@Value("${app.upload.dir:src/main/resources/static/uploads}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir);
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory: " + uploadPath, e);
        }
    }

    public String encodeImageToBase64(String imageUrl) {
        if (imageUrl.startsWith("http")) {
            return imageUrl;
        }

        try {
            Path imagePath = uploadPath.resolve(imageUrl);
            String mimeType = Files.probeContentType(imagePath);
            byte[] fileContent = Files.readAllBytes(imagePath);

            return String.format("data:%s;base64,%s",
                    mimeType != null ? mimeType : "image/jpeg",
                    Base64.getEncoder().encodeToString(fileContent));
        } catch (IOException e) {
            return imageUrl;
        }
    }

    @SneakyThrows
    public String saveImage(String newImage, String oldImage) {
        if (isSameImage(newImage, oldImage)) {
            return oldImage;
        }

        deleteImage(oldImage);
        final var imageBytes = decodeImage(newImage);

        String fileName = UUID.randomUUID().toString() + ".jpg";
        Path destinationFile = uploadPath.resolve(fileName);

        Files.write(destinationFile, imageBytes);

        return fileName;
    }

    private void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.startsWith("http")) {
            try {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                Path filePath = uploadPath.resolve(fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.error("Error while deleting image: {}", e.getMessage());
            }
        }
    }

    private byte[] decodeImage(String base64Image) {
        String[] parts = base64Image.split(",");
        String imageData = parts.length > 1 ? parts[1] : parts[0];

        return Base64.getDecoder().decode(imageData);
    }

    private boolean isSameImage(String newImageBase64, String existingImageUrl) {
        if (existingImageUrl == null || newImageBase64 == null) {
            return false;
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Get checksum of new image
            final var newImageBytes = decodeImage(newImageBase64);
            byte[] newHash = md.digest(newImageBytes);

            // Get checksum of existing image
            String fileName = existingImageUrl.substring(existingImageUrl.lastIndexOf("/") + 1);
            byte[] existingImageBytes = Files.readAllBytes(uploadPath.resolve(fileName));
            md.reset();
            byte[] existingHash = md.digest(existingImageBytes);

            return Arrays.equals(newHash, existingHash);
        } catch (Exception e) {
            return false;
        }
    }
}