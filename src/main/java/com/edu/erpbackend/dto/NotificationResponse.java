package com.edu.erpbackend.dto;

import com.edu.erpbackend.model.operations.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private UUID notificationId;           // Unique ID for each notification
    private UUID branchId;                 // Which branch (null if "ALL")
    private Integer semester;              // Which semester (null if not class-specific)
    private String batchYear;              // Batch year like "2023-2027" (null if not batch-specific)
    private String title;
    private String message;
    private NotificationType type;
    private UUID referenceId;              // ID of related object (Assignment, Doubt, etc.)
    private boolean isRead;
    private LocalDateTime createdAt;
    private String targetGroup;            // "ALL", "BATCH", "CLASS", "USER"
    private String attachmentUrl;
}

