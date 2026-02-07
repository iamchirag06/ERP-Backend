package com.edu.erpbackend.controller.users;

import com.edu.erpbackend.dto.StudentProfileResponse;
import com.edu.erpbackend.dto.StudentProfileUpdateRequest;
import com.edu.erpbackend.service.users.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/student/profile")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // 1. GET MY PROFILE
    @GetMapping
    public ResponseEntity<StudentProfileResponse> getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(studentService.getMyProfile(email));
    }

    // 2. UPDATE PROFILE
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody StudentProfileUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        studentService.updateProfile(email, request);
        return ResponseEntity.ok("Profile updated successfully");
    }
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            String newImageUrl = studentService.uploadProfileImage(email, file);

            // Return the new URL in a JSON object
            return ResponseEntity.ok(Map.of("imageUrl", newImageUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}