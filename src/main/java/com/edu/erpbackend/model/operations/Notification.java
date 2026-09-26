package com.edu.erpbackend.model.operations;

import com.edu.erpbackend.model.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    // ✅ CASCADE: delete recipient → delete their notifications
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User recipient;

    private String title;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private UUID referenceId;
    private UUID batchId;
    private UUID branchId;
    private Integer semester;

    private boolean isRead = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private String targetGroup = "ALL";

    private String attachmentUrl;
}
