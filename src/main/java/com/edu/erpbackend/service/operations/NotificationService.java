package com.edu.erpbackend.service.operations;

import com.edu.erpbackend.dto.NotificationResponse;
import com.edu.erpbackend.model.operations.Branch;
import com.edu.erpbackend.model.operations.Notification;
import com.edu.erpbackend.model.operations.NotificationType;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.academic.NotificationRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public void sendToUser(User user, String title, String message, NotificationType type, UUID referenceId) {
        Notification notification = Notification.builder()
                .recipient(user)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    // ✅ Fixed: Added 'String attachmentUrl' parameter
    public UUID sendToGroup(Branch branch, Integer semester, String title, String message, NotificationType type, UUID referenceId, String attachmentUrl) {
        List<Student> students = studentRepository.findByBranchAndSemester(branch, semester);

        if (students.isEmpty()) return null;
        UUID batchId = UUID.randomUUID();

        List<Notification> notifications = students.stream()
                .map(student -> buildNotification(
                        student, title, message, type, referenceId,
                        branch.getId(), semester, batchId, "CLASS", attachmentUrl
                ))
                .toList();

        notificationRepository.saveAll(notifications);
        return batchId;
    }

    // ✅ Fixed: Added 'String attachmentUrl' parameter
    public UUID sendToAll(String title, String message, String attachmentUrl) {
        List<User> allUsers = userRepository.findAll();
        UUID batchId = UUID.randomUUID();

        List<Notification> notifications = allUsers.stream()
                .map(user -> buildNotification(
                        user, title, message, NotificationType.NOTICE, null,
                        null, null, batchId, "ALL", attachmentUrl
                ))
                .toList();

        notificationRepository.saveAll(notifications);
        return batchId;
    }

    public UUID sendToBatch(String batch, String title, String message, String attachmentUrl) {
        // 1. Find all students in this batch (2023-2027)
        List<Student> students = studentRepository.findByBatch(batch);

        if (students.isEmpty()) return null;
        UUID batchId = UUID.randomUUID(); // Unique ID for this "blast"

        // 2. Create a notification for each student
        List<Notification> notifications = students.stream()
                .map(student -> buildNotification(
                        student, title, message, NotificationType.NOTICE, null,
                        null, null, batchId, "BATCH", attachmentUrl
                ))
                .toList();

        notificationRepository.saveAll(notifications);
        return batchId;
    }

    // ✅ NEW: Helper method to build notifications (DRY principle)
    private Notification buildNotification(
            User recipient, String title, String message,
            NotificationType type, UUID referenceId,
            UUID branchId, Integer semester, UUID batchId,
            String targetGroup, String attachmentUrl
    ) {
        return Notification.builder()
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .branchId(branchId)
                .semester(semester)
                .batchId(batchId)
                .targetGroup(targetGroup)
                .attachmentUrl(attachmentUrl)
                .isRead(false)
                .build();
    }

    // ✅ NEW: Get notifications as DTOs with branchId included
    public List<NotificationResponse> getMyNotificationsAsDTO(UUID userId) {
        List<Notification> notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ NEW: Convert Notification entity to Response DTO
    private NotificationResponse convertToDTO(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .branchId(notification.getBranchId())
                .semester(notification.getSemester())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .referenceId(notification.getReferenceId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .targetGroup(notification.getTargetGroup())
                .attachmentUrl(notification.getAttachmentUrl())
                .build();
    }

    public void markAsRead(UUID id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Transactional
    public void withdrawNotification(UUID batchId) {
        notificationRepository.deleteByBatchId(batchId);
    }
    // ✅ NEW: Unread count
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    // ✅ NEW: Mark all notifications as read
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unread = notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(n -> !n.isRead())
                .toList();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}