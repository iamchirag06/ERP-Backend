package com.edu.erpbackend.controller.users;

import com.edu.erpbackend.dto.TeacherProfileResponse;
import com.edu.erpbackend.dto.TeacherProfileUpdateRequest;
import com.edu.erpbackend.service.users.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher/profile")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    // 1. GET PROFILE
    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TeacherProfileResponse> getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(teacherService.getMyProfile(email));
    }

    // 2. UPDATE PROFILE (Text)
    @PutMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateProfile(@RequestBody TeacherProfileUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        teacherService.updateProfile(email, request);
        return ResponseEntity.ok("Teacher profile updated successfully");
    }

    // 3. UPLOAD IMAGE
    @PostMapping("/image")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            String newImageUrl = teacherService.uploadProfileImage(email, file);
            return ResponseEntity.ok(Map.of("imageUrl", newImageUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}