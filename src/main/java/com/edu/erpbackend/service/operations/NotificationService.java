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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    // ✅ NEW: Delete notification by ID
    @Transactional
    public void deleteNotification(UUID notificationId) {
        notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationRepository.deleteById(notificationId);
    }

    // ✅ NEW: Delete all notifications with same batch ID (sent to multiple users)
    // Returns count of deleted notifications
    @Transactional
    public long deleteNotificationBatch(UUID batchId) {
        List<Notification> notificationsWithBatch = notificationRepository.findByBatchId(batchId);
        
        if (notificationsWithBatch.isEmpty()) {
            throw new RuntimeException("No notifications found with batch ID: " + batchId);
        }
        
        notificationRepository.deleteByBatchId(batchId);
        return notificationsWithBatch.size();
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

    // ✅ NEW: Get all sent notifications (distinct batch IDs with details)
    public List<LinkedHashMap<String, Object>> getAllSentNotifications() {
        List<UUID> batchIds = notificationRepository.findAllDistinctBatchIds();
        
        return batchIds.stream()
                .map(batchId -> {
                    List<Notification> notificationsInBatch = notificationRepository.findByBatchId(batchId);
                    if (!notificationsInBatch.isEmpty()) {
                        Notification first = notificationsInBatch.get(0);
                        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                        map.put("batchId", batchId.toString());
                        map.put("title", first.getTitle());
                        map.put("message", first.getMessage());
                        map.put("type", first.getType().toString());
                        map.put("targetGroup", first.getTargetGroup());
                        map.put("createdAt", first.getCreatedAt().toString());
                        map.put("recipientCount", notificationsInBatch.size());
                        return map;
                    }
                    return null;
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    // ✅ NEW: Get details of a specific batch
    public Map<String, Object> getBatchDetails(UUID batchId) {
        List<Notification> notificationsInBatch = notificationRepository.findByBatchId(batchId);
        
        if (notificationsInBatch.isEmpty()) {
            throw new RuntimeException("Batch not found");
        }
        
        Notification first = notificationsInBatch.get(0);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("batchId", batchId.toString());
        result.put("title", first.getTitle());
        result.put("message", first.getMessage());
        result.put("type", first.getType().toString());
        result.put("targetGroup", first.getTargetGroup());
        result.put("branchId", first.getBranchId() != null ? first.getBranchId().toString() : null);
        result.put("semester", first.getSemester());
        result.put("createdAt", first.getCreatedAt().toString());
        result.put("totalRecipients", notificationsInBatch.size());
        result.put("readCount", notificationsInBatch.stream().filter(Notification::isRead).count());
        result.put("unreadCount", notificationsInBatch.stream().filter(n -> !n.isRead()).count());
        result.put("attachmentUrl", first.getAttachmentUrl());
        return result;
    }
}