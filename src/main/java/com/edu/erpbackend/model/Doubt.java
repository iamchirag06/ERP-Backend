package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "doubts")
@Data
public class Doubt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "asker_id", nullable = false)
    private Student asker; // ✅ Your existing field

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject; // 🆕 Added: To filter doubts by subject

    @Column(nullable = false)
    private String title; // ✅ Your existing field

    @Column(columnDefinition = "TEXT")
    private String description; // 🆕 Added: For full question details

    @Column(name = "bounty_points")
    private Integer bountyPoints = 0; // ✅ Your existing field

    @Enumerated(EnumType.STRING)
    private DoubtStatus status = DoubtStatus.OPEN; // ✅ Your existing field

    @CreationTimestamp
    private LocalDateTime createdAt; // 🆕 Added: To sort by latest
}