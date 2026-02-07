package com.edu.erpbackend.model.operations;


import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "branches")
@Data
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Matches 'uuid id PK'

    @Column(nullable = false, unique = true)
    private String name; // Matches 'string name' (e.g., "Computer Science")

    @Column(nullable = false, unique = true)
    private String code; // Matches 'string code' (e.g., "CSE")
}