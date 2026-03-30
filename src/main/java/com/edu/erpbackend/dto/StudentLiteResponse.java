package com.edu.erpbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class StudentLiteResponse {
    private UUID studentId;
    private String name;
    private String rollNo;
}