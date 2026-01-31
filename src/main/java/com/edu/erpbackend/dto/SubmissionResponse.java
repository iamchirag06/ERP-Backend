package com.edu.erpbackend.dto;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class SubmissionResponse {
    private UUID submissionId;
    private String studentName;
    private String rollNo;
    private String submissionLink; // The File URL
    private String grade;
    private String feedback;
    private boolean isLate;
    private LocalDateTime submittedAt;

    // Constructor to easily map from Entity
    public SubmissionResponse(UUID id, String name, String roll, String link, String grade, String feedback, boolean late, LocalDateTime date) {
        this.submissionId = id;
        this.studentName = name;
        this.rollNo = roll;
        this.submissionLink = link;
        this.grade = grade;
        this.feedback = feedback;
        this.isLate = late;
        this.submittedAt = date;
    }
}