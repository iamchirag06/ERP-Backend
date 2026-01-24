package com.edu.erpbackend.dto;

import com.edu.erpbackend.model.Role;
import lombok.Data;
import java.util.UUID;

@Data
public class RegisterRequest {
    // Common fields
    private String email;
    private String password;
    private Role role; // STUDENT or TEACHER

    // Student specific
    private String rollNo;
    private String branchCode; // We send the code (e.g., "CSE"), backend finds the ID
    private Integer semester;

    // Teacher specific
    private String employeeId;
    private String departmentId;
}