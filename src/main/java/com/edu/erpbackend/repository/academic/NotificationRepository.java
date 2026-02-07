package com.edu.erpbackend.repository.academic;

import com.edu.erpbackend.model.operations.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    // Get latest notifications first
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    // Delete by Batch ID
    void deleteByBatchId(UUID batchId);

    long countByRecipientIdAndIsReadFalse(UUID recipientId);
}