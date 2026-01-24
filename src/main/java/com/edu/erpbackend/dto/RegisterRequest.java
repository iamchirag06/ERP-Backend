package com.edu.erpbackend.dto;

import com.edu.erpbackend.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private Role role;
    private String name;

    // ✅ Student Fields
    private String rollNo;
    private Integer semester;
    private String branchCode; // Optional: If you want to link branch by code

    // ✅ Teacher Fields
    private String employeeId;
    private String department;
}