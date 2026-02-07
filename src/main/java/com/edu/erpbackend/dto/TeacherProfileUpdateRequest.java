package com.edu.erpbackend.dto;

import lombok.Data;

@Data
public class TeacherProfileUpdateRequest {
    private String phoneNumber;
    private String cabinNumber;
    private String qualification;

}