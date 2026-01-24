package com.edu.erpbackend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AttendanceRequest {
    private UUID subjectId;
    private LocalDate date;
    private Integer semester;

    // We will send a list of Student IDs who are PRESENT.
    // Anyone NOT in this list for this class will be marked ABSENT automatically.
    private List<UUID> presentStudentIds;
}