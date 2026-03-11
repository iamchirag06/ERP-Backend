package com.edu.erpbackend.service.common;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {

    private final Cloudinary cloudinary;

    // 1. Upload Method (Existing)
    public String saveFile(MultipartFile file, String folderName) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", folderName));
            return (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed");
        }
    }

    // 2. Delete Method (New) 🗑️
    public void deleteFile(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            // Log error but don't stop the process (it's okay if delete fails)
            System.err.println("Failed to delete old image: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        // Simple logic: split by "/" and remove extension
        String[] parts = imageUrl.split("/");
        String filenameWithExt = parts[parts.length - 1]; // "image.jpg"
        String filename = filenameWithExt.split("\\.")[0]; // "image"
        String folder = parts[parts.length - 2]; // "profiles"
        return folder + "/" + filename;
    }
}