package com.edu.erpbackend.model.academic;

import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.operations.Branch;
import com.nimbusds.openid.connect.sdk.SubjectType;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "subjects")
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code; // ✅ Added (e.g., "CS-101")

    @Column(nullable = false)
    private Integer credits; // ✅ Added (e.g., 4)

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    // 🆕 ADD THIS: Maps the subject to a specific semester (e.g., 3)
    @Column(nullable = false)
    private Integer semester;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    private SubjectType type; // THEORY, PRACTICAL, ELECTIVE

    // 2. Syllabus Tracking
    private String syllabusUrl; // Link to PDF of syllabus

    @Entity
    @Table(name = "course_mapping")
    @Data
    public static class CourseMapping {

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
}