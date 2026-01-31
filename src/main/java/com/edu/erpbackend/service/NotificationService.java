package com.edu.erpbackend.service;

import com.edu.erpbackend.model.*;
import com.edu.erpbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
                .map(student -> Notification.builder()
                        .recipient(student)
                        .title(title)
                        .message(message)
                        .type(type)
                        .referenceId(referenceId)
                        .batchId(batchId)
                        .attachmentUrl(attachmentUrl) // ✅ Now works
                        .isRead(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
        return batchId;
    }

    // ✅ Fixed: Added 'String attachmentUrl' parameter
    public UUID sendToAll(String title, String message, String attachmentUrl) {
        List<User> allUsers = userRepository.findAll();
        UUID batchId = UUID.randomUUID();

        List<Notification> notifications = allUsers.stream()
                .map(user -> Notification.builder()
                        .recipient(user)
                        .title(title)
                        .message(message)
                        .type(NotificationType.NOTICE)
                        .batchId(batchId)
                        .attachmentUrl(attachmentUrl) // ✅ Now works
                        .isRead(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
        return batchId;
    }

    public List<Notification> getMyNotifications(UUID userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
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
}