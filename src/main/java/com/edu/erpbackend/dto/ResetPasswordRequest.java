package com.edu.erpbackend.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token; // The secret code sent to email
    private String newPassword;
}