package com.edu.erpbackend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class NoticeRequest {
    private String title;
    private String message;
    private UUID branchId; // Optional (if null, sends to whole college)
    private Integer semester; // Optional.
    private  String batch;
}