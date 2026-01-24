package com.edu.erpbackend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class SubmissionRequest {
    private UUID assignmentId;
    private String submissionLink; // e.g., Google Drive Link
}