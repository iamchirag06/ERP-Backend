package com.edu.erpbackend.service.common;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final Cloudinary cloudinary;

    public String saveFile(MultipartFile file, String folderName) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        String publicId = UUID.randomUUID().toString();

        String resourceType = "auto";

        // Detect Documents (PDF, DOCX, ZIP) and switch to "raw"
        if (contentType != null && !contentType.startsWith("image/") && !contentType.startsWith("video/")) {
            resourceType = "raw";
            if (originalFilename != null && originalFilename.contains(".")) {
                publicId += originalFilename.substring(originalFilename.lastIndexOf("."));
            }
        }

        byte[] fileBytes = file.getBytes();
        if (fileBytes.length == 0) throw new IOException("File is empty!");

        // Upload to Cloudinary with Folder
        Map uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.asMap(
                "public_id", publicId,
                "resource_type", resourceType,
                "folder", folderName
        ));

        return uploadResult.get("secure_url").toString();
    }
}