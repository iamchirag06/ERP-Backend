package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "assignments")
@Data
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Matches 'uuid id PK'

    // Which subject is this for?
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject; // Matches 'uuid subject_id FK' (given_in)

    private String title; // Matches 'string title' (Not explicitly in diagram but implied)

    // Note: Diagram doesn't explicitly list description/due_date in the box,
    // but usually you need them. I will add them as per standard ERP needs.
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;
}
