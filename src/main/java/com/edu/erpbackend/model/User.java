package com.edu.erpbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // This handles the "extends" relationship
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Matches 'uuid id PK'

    @Column(unique = true, nullable = false)
    private String email; // Matches 'string email'

    @Column(name = "password_hash", nullable = false)
    private String passwordHash; // Matches 'string password_hash'

    @Enumerated(EnumType.STRING)
    private Role role; // Matches 'enum role'

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // Matches 'timestamp created_at'
}