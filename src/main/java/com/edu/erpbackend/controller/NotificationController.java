package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.NoticeRequest;
import com.edu.erpbackend.model.*;
import com.edu.erpbackend.repository.BranchRepository;
import com.edu.erpbackend.repository.UserRepository;
import com.edu.erpbackend.service.FileService;
import com.edu.erpbackend.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper; // ✅ Import 1
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final FileService fileService;

    // ✅ Initialize ObjectMapper manually (fixes parsing issues)
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Get MY Notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(notificationService.getMyNotifications(user.getId()));
    }

    // Mark as Read
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Marked as read");
    }

    // Send Manual Notice (Teacher/Admin)
    @PostMapping(value = "/send", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> sendNotice(
            @RequestPart("data") NoticeRequest request, // Ensure DTO has 'batch' field
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        // 1. Upload File (Reusable Logic)
        String attachmentUrl = fileService.saveFile(file, "Notices");
        UUID notificationReferenceId; // Returns the ID of the created notice

        // 2. Logic to determine recipients
        if (request.getBatch() != null && !request.getBatch().isEmpty()) {
            // ✅ CASE 1: Send to a specific Batch (e.g., "2023-2027")
            notificationReferenceId = notificationService.sendToBatch(
                    request.getBatch(),
                    request.getTitle(),
                    request.getMessage(),
                    attachmentUrl
            );
        } else if (request.getBranchId() != null && request.getSemester() != null) {
            // ✅ CASE 2: Send to a specific Class (Branch + Semester)
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));

            notificationReferenceId = notificationService.sendToGroup(
                    branch,
                    request.getSemester(),
                    request.getTitle(),
                    request.getMessage(),
                    NotificationType.NOTICE,
                    null,
                    attachmentUrl
            );
        } else {
            // ✅ CASE 3: Send to Everyone (Default)
            notificationReferenceId = notificationService.sendToAll(
                    request.getTitle(),
                    request.getMessage(),
                    attachmentUrl
            );
        }
        return ResponseEntity.ok(Map.of(
                "message", "Notice sent successfully!",
                "id", notificationReferenceId
        ));
    }

    // Withdraw / Unsend Notice
    @DeleteMapping("/batch/{batchId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> withdrawNotice(@PathVariable UUID batchId) {
        notificationService.withdrawNotification(batchId);
        return ResponseEntity.ok("Notice withdrawn from all students.");
    }
}