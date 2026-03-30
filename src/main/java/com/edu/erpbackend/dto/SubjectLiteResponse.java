package com.edu.erpbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SubjectLiteResponse {
    private UUID subjectId;
    private String name;
    private String code;
    private Integer semester;

    private UUID branchId;
    private String branchName;
}