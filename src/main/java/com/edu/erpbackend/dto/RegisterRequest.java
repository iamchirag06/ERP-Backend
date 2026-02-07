package com.edu.erpbackend.dto;

import com.edu.erpbackend.model.users.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID; // Import UUID

@Data
public class RegisterRequest {// Existing fields
    private String name;
    private String email;
    private String password;
    private Role role; // STUDENT, TEACHER, ADMIN

    // Student Specific (Existing)
    private String rollNo;
    private String batch;
    private UUID branchId;
    private Integer semester;

    // ✅ NEW: Student Personal & Guardian Info
    private String phoneNumber;
    private String address;
    private LocalDate dob;
    private String guardianName;
    private String guardianPhone;

    // ✅ NEW: Student Academic Info
    private Double cgpa;
    private Integer activeBacklogs;
    private String linkedinProfile;

    // ✅ NEW: Teacher Specific
    private String designation; // "HOD", "ASSISTANT_PROFESSOR"
    private String qualification;
    private String cabinNumber;
    private LocalDate joiningDate;
}