package com.edu.erpbackend.model.users;

import com.edu.erpbackend.model.operations.Branch;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Entity
@Table(name = "teachers")
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class Teacher extends User { // ✅ Inherits setPasswordHash()

    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @Column(nullable = false)
    private String department; // ✅ Fixes 'setDepartment' error

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // 1. Professional Details
    private String qualification; // e.g., "Ph.D. in AI", "M.Tech"

    @Enumerated(EnumType.STRING)
    private Student.Designation designation; // ASST_PROFESSOR, ASSOC_PROFESSOR, HOD

    private LocalDate joiningDate;
    private String phoneNumber;
    private String profileImageUrl;

    // 2. Office Location (For students to find them)
    private String cabinNumber; // e.g., "Block B, Room 204"
}
