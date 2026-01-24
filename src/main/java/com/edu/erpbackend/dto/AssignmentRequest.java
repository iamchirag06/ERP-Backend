package com.edu.erpbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AssignmentRequest {
    private String title;
    private String description;
    private LocalDateTime deadline; // Format: "2026-01-30T23:59:00"
    private UUID subjectId;
}