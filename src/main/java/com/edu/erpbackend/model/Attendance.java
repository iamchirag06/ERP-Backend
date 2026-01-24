package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "attendance")
@Data
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Matches 'uuid id PK'

    // Who was marked?
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Matches 'uuid student_id FK'

    // For which class?
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject; // Matches 'uuid subject_id FK'

    @Column(nullable = false)
    private LocalDate date; // Matches 'date date'

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status; // Matches 'enum status'
}

