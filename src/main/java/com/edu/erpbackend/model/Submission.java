package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "submissions")
@Data
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(columnDefinition = "TEXT")
    private String submissionLink;

    private Integer grade;

    @Column(columnDefinition = "TEXT")
    private String teacherFeedback;

    @Column(nullable = false)
    private boolean isLate;

    @CreationTimestamp
    private LocalDateTime submittedAt;
}