package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "user_id")
public class Student extends User { // ✅ Inherits setPasswordHash()

    @Column(name = "roll_no", unique = true)
    private String rollNo;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    private Integer semester;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    @Column(name = "admission_date")
    private LocalDate admissionDate; // ✅ Fixes 'setAdmissionDate' error
}