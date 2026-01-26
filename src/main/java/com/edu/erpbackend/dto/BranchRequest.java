package com.edu.erpbackend.dto;

import lombok.Data;

@Data
public class BranchRequest {
    private String name; // e.g., "Computer Science"
    private String code; // 👈 Add this (e.g., "CSE")
}