package com.edu.erpbackend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class TeacherProfileResponse {
    // Identity
    private String name;
    private String email;
    private String profileImageUrl;

    // Professional
    private String designation;   // e.g. "ASSISTANT_PROFESSOR"
    private String qualification; // e.g. "PhD in AI"
    private String branchName;    // e.g. "Computer Science"
    private LocalDate joiningDate;

    // Contact & Location (Editable)
    private String phoneNumber;
    private String cabinNumber;   // e.g. "Block B, 204"
}