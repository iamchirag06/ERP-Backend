package com.edu.erpbackend.model;


import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "solutions")
@Data
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Matches 'uuid id PK'

    // Which doubt is this answering?
    @ManyToOne
    @JoinColumn(name = "doubt_id", nullable = false)
    private Doubt doubt; // Matches 'uuid doubt_id FK'

    // Who provided the answer?
    @ManyToOne
    @JoinColumn(name = "solver_id", nullable = false)
    private Student solver; // Matches 'uuid solver_id FK'

    @Column(name = "is_accepted")
    private Boolean isAccepted = false; // Matches 'boolean is_accepted'
}