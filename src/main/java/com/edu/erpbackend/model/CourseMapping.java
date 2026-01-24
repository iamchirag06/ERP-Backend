package com.edu.erpbackend.model;


import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "course_mapping")
@Data
public class CourseMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Matches 'uuid id PK'

    // Link to Teacher
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher; // Matches 'uuid teacher_id FK'

    // Link to Subject
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject; // Matches 'uuid subject_id FK'

    @Column(name = "academic_year")
    private String academicYear; // Matches 'string academic_year'
}
