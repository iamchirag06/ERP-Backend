package com.edu.erpbackend.repository.academic;

import com.edu.erpbackend.model.operations.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    // Get latest notifications first
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    // Find all notifications by batch ID
    List<Notification> findByBatchId(UUID batchId);

    // Delete by Batch ID
    void deleteByBatchId(UUID batchId);

    long countByRecipientIdAndIsReadFalse(UUID recipientId);

    // ✅ NEW: Get all distinct batch IDs (for admin/teacher to see sent notifications)
    // Uses GROUP BY to get unique batches ordered by most recent
    @Query(value = "SELECT n.batch_id FROM notifications n WHERE n.batch_id IS NOT NULL GROUP BY n.batch_id ORDER BY MAX(n.created_at) DESC", nativeQuery = true)
    List<UUID> findAllDistinctBatchIds();
}