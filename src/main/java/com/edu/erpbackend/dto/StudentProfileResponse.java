package com.edu.erpbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentProfileResponse {
    // Identity
    private String name;
    private String email;
    private String rollNo;
    private String profileImageUrl;

    // Academic
    private String branchName;
    private Integer semester;
    private String batch;
    private Double cgpa;
    private Integer activeBacklogs;

    // Personal (Editable)
    private String phoneNumber;
    private String address;
    private List<String> skills;
    private String linkedinProfile;
    private String githubProfile;

    // Guardian
    private String guardianName;
    private String guardianPhone;
}