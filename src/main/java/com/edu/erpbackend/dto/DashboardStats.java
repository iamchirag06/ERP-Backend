package com.edu.erpbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {
    // Student Stats
    private Double attendancePercentage;
    private Integer pendingAssignments;

    // Teacher Stats
    private Integer totalStudents;

    // Common
    private Integer activeNotices;

    private Integer ungradedAssignments;
}