package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "submissions")
@Data
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Matches 'uuid id PK'

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment; // Matches 'uuid assignment_id FK'

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // Matches 'uuid student_id FK'

    @Column(name = "file_url")
    private String fileUrl; // Matches 'string file_url'

    @Column(name = "marks_obtained")
    private Float marksObtained; // Matches 'float marks_obtained'
}