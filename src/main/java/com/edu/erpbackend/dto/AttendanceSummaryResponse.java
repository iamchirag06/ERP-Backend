package com.edu.erpbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceSummaryResponse {
    private String subjectName;
    private String subjectCode;
    private int totalClasses;
    private int presentClasses;
    private double percentage;
}