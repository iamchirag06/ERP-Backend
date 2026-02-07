package com.edu.erpbackend.dto;

import lombok.Data;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class TimetableRequest {
    private UUID subjectId;
    private UUID teacherId;
    private UUID branchId;
    private Integer semester;
    private String roomNumber;
    private String day;        // e.g., "MONDAY"
    private LocalTime startTime; // e.g., "10:00"
    private LocalTime endTime;   // e.g., "11:00"
}