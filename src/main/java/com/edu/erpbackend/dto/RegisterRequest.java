package com.edu.erpbackend.dto;

import com.edu.erpbackend.model.Role;
import lombok.Data;
import java.util.UUID; // Import UUID

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;

    // Student specific
    private String rollNo;
    private Integer semester;
    private UUID branchId; // 👈 Add this line!

    // Teacher specific
    private String employeeId;
    private String department;
}