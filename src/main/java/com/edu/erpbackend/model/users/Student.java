package com.edu.erpbackend.model.users;

import com.edu.erpbackend.model.operations.Branch;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User { // ✅ Inherits setPasswordHash()

    @Column(name = "roll_no", unique = true)
    private String rollNo;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    private Integer semester;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    @Column(name = "admission_date")
    private LocalDate admissionDate; // ✅ Fixes 'setAdmissionDate' error

    @Column(name = "batch")
    private String batch;

    private String phoneNumber;
    private String address;
    private String profileImageUrl; // For UI avatar
    private LocalDate dob; // Date of Birth

    // 2. Guardian Info (For Emergency/Absence Alerts)
    private String guardianName;
    private String guardianPhone;

    // 3. Placement Data (CRITICAL for TPO Module)
    private Double cgpa; // e.g., 8.5
    private Integer activeBacklogs; // 0 means eligible for placement

    @ElementCollection // Stores a list of strings in a separate table
    private List<String> skills; // e.g., ["Java", "React", "AWS"]

    private String linkedinProfile;
    private String githubProfile;

    public enum Designation {
        PROFESSOR,
        ASSOCIATE_PROFESSOR,
        ASSISTANT_PROFESSOR,
        LAB_ASSISTANT,
        HOD,
        PRINCIPAL
    }
}