package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The user who RECEIVES the notification
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    // Optional: ID of the related object (Assignment ID, Doubt ID) so frontend can click it
    private UUID referenceId;

    private UUID batchId;

    private boolean isRead = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private String targetGroup = "ALL";

    private String attachmentUrl;
}