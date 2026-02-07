package com.edu.erpbackend.model.academic;

import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.Teacher;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class StudyMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title; // "Unit 1: Introduction to Java"
    private String description;
    private String fileUrl; // URL from FileService
    private String fileType; // PDF, PPT, DOC

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_teacher_id")
    private Teacher uploadedBy;

    private LocalDateTime uploadedAt = LocalDateTime.now();

    // Tagging
    private String unitTag; // "Unit 1", "Unit 2", "Previous Year Papers"

    @Entity
    @Table(name = "solutions")
    @Data
    public static class Solution {

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
}