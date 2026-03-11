package com.edu.erpbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class StudentProfileUpdateRequest {
    private String phoneNumber;
    private String address;
    private String linkedinProfile;
    private String githubProfile;
    private String profileImageUrl;
    private List<String> skills;
}