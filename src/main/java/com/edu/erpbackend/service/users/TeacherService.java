package com.edu.erpbackend.service.users;

import com.edu.erpbackend.dto.TeacherProfileResponse;
import com.edu.erpbackend.dto.TeacherProfileUpdateRequest;
import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.users.TeacherRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import com.edu.erpbackend.service.common.FileService; // Ensure this imports your Cloudinary logic
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final FileService fileService;

    // 1. GET PROFILE
    public TeacherProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Teacher teacher = teacherRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        return TeacherProfileResponse.builder()
                .name(teacher.getName())
                .email(teacher.getEmail())
                .profileImageUrl(teacher.getProfileImageUrl())
                .designation(teacher.getDesignation() != null ? teacher.getDesignation().toString() : "N/A")
                .qualification(teacher.getQualification())
                .branchName(teacher.getBranch() != null ? teacher.getBranch().getName() : "General")
                .joiningDate(teacher.getJoiningDate())
                .phoneNumber(teacher.getPhoneNumber())
                .cabinNumber(teacher.getCabinNumber())
                .build();
    }

    // 2. UPDATE PROFILE (Text Fields Only)
    public void updateProfile(String email, TeacherProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Teacher teacher = teacherRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        if (request.getPhoneNumber() != null) teacher.setPhoneNumber(request.getPhoneNumber());
        if (request.getCabinNumber() != null) teacher.setCabinNumber(request.getCabinNumber());
        if (request.getQualification() != null) teacher.setQualification(request.getQualification());

        teacherRepository.save(teacher);
    }

    // 3. UPLOAD PROFILE IMAGE
    public String uploadProfileImage(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Teacher teacher = teacherRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        // Delete old image if it exists
        if (teacher.getProfileImageUrl() != null && !teacher.getProfileImageUrl().isEmpty()) {
            fileService.deleteFile(teacher.getProfileImageUrl());
        }

        // Upload new image
        String newUrl = fileService.saveFile(file, "erp_profiles");

        // Save URL to DB
        teacher.setProfileImageUrl(newUrl);
        teacherRepository.save(teacher);

        return newUrl;
    }
}