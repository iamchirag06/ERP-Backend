package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id") // This links 'user_id' FK to the User table
public class Student extends User {

    @Column(name = "roll_no", unique = true)
    private String rollNo; // Matches 'string roll_no'

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch; // <--- ADD THIS NOW to match 'uuid branch_id FK'

    private Integer semester; // Matches 'int semester'

    @Column(name = "total_points")
    private Integer totalPoints = 0; // Matches 'int total_points'
}