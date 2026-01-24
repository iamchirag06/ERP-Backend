package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
}