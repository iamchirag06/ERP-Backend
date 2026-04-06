package com.edu.erpbackend.controller.operations;

import com.edu.erpbackend.dto.NoticeRequest;
import com.edu.erpbackend.dto.NotificationResponse;
import com.edu.erpbackend.model.operations.Branch;
import com.edu.erpbackend.model.operations.NotificationType;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.academic.BranchRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import com.edu.erpbackend.service.common.FileService;
import com.edu.erpbackend.service.operations.NotificationService;
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
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final FileService fileService;

    // ✅ Helper method to extract current user ID (DRY principle)
    private UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getId();
    }

    // Get MY Notifications
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotificationsAsDTO(getCurrentUserId()));
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
            @RequestPart("data") String dataJson, // Accept as String to avoid RN Content-Type issues
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        // Parse the JSON string manually
        ObjectMapper mapper = new ObjectMapper();
        NoticeRequest request = mapper.readValue(dataJson, NoticeRequest.class);

        // 1. Upload File (Reusable Logic)
        String attachmentUrl = null;
        if (file != null && !file.isEmpty()) {
            attachmentUrl = fileService.saveFile(file, "Notices");
        }
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
     // Get unread notification count (for frontend bell badge 🔔)
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount() {
        long count = notificationService.getUnreadCount(getCurrentUserId());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    // Mark ALL notifications as read at once
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllRead() {
        notificationService.markAllAsRead(getCurrentUserId());
        return ResponseEntity.ok("All notifications marked as read");
    }
}