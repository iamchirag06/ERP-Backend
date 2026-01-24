package com.edu.erpbackend.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "teachers")
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id") // This links 'user_id' FK to the User table
public class Teacher extends User {

    @Column(name = "employee_id", unique = true)
    private String employeeId; // Matches 'string employee_id'

    // We will add 'department_id' later
    // private UUID departmentId;
}