package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solutions")
@Data
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doubt_id", nullable = false)
    private Doubt doubt; // ✅ Your existing field

    @ManyToOne
    @JoinColumn(name = "solver_id", nullable = false)
    private Student solver; // ✅ Your existing field

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 🆕 Added: The actual answer text!

    @Column(name = "is_accepted")
    private Boolean isAccepted = false; // ✅ Your existing field

    @CreationTimestamp
    private LocalDateTime createdAt; // 🆕 Added
}