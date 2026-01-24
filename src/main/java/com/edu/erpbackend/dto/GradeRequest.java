package com.edu.erpbackend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class GradeRequest {
    private UUID submissionId;
    private Integer grade; // e.g., 85
    private String feedback; // e.g., "Good work, but fix the indentation."
}