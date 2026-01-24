package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "doubts")
@Data
public class Doubt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Matches 'uuid id PK'

    // Who asked the question?
    @ManyToOne
    @JoinColumn(name = "asker_id", nullable = false)
    private Student asker; // Matches 'uuid asker_id FK'

    @Column(nullable = false)
    private String title; // Matches 'string title'

    @Column(name = "bounty_points")
    private Integer bountyPoints; // Matches 'int bounty_points'

    @Enumerated(EnumType.STRING)
    private DoubtStatus status; // Matches 'enum status'
}

enum DoubtStatus {
    OPEN,
    SOLVED
}