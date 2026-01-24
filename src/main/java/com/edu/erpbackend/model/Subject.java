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
    private UUID id; // Matches 'uuid id PK'

    @Column(nullable = false)
    private String name; // Matches 'string name' (e.g., "Data Structures")

    // The Relationship: A Subject belongs to a Branch
    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch; // Matches the link from BRANCHES to SUBJECTS
}