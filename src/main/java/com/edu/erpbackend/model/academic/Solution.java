package com.edu.erpbackend.model.academic;

import com.edu.erpbackend.model.users.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solutions")
@Data
public class Solution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doubt_id", nullable = false)
    private Doubt doubt;

    // ✅ Changed from Student to User so Teachers can answer too!
    @ManyToOne
    @JoinColumn(name = "solver_id", nullable = false)
    private User solver;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Boolean accepted = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}