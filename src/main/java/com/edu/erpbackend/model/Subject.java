package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "subjects")
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code; // ✅ Added (e.g., "CS-101")

    @Column(nullable = false)
    private Integer credits; // ✅ Added (e.g., 4)

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    // 🆕 ADD THIS: Maps the subject to a specific semester (e.g., 3)
    @Column(nullable = false)
    private Integer semester;
}