package com.edu.erpbackend.service.users;

import com.edu.erpbackend.dto.StudentProfileResponse;
import com.edu.erpbackend.dto.StudentProfileUpdateRequest;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.edu.erpbackend.service.common.FileService;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final FileService fileService; // ✅ Inject this

    // 1. GET PROFILE LOGIC
    public StudentProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        return StudentProfileResponse.builder()
                .name(student.getName())
                .email(student.getEmail())
                .rollNo(student.getRollNo())
                .profileImageUrl(student.getProfileImageUrl())
                .branchName(student.getBranch() != null ? student.getBranch().getName() : "Not Assigned") // ✅ Null Safety
                .semester(student.getSemester())
                .batch(student.getBatch())
                .cgpa(student.getCgpa())
                .activeBacklogs(student.getActiveBacklogs())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .skills(student.getSkills())
                .linkedinProfile(student.getLinkedinProfile())
                .githubProfile(student.getGithubProfile())
                .guardianName(student.getGuardianName())
                .guardianPhone(student.getGuardianPhone())
                .build();
    }

    // 2. UPDATE PROFILE LOGIC
    public void updateProfile(String email, StudentProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        if (request.getPhoneNumber() != null) student.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) student.setAddress(request.getAddress());
        if (request.getLinkedinProfile() != null) student.setLinkedinProfile(request.getLinkedinProfile());
        if (request.getGithubProfile() != null) student.setGithubProfile(request.getGithubProfile());
        if (request.getSkills() != null) student.setSkills(request.getSkills());

        studentRepository.save(student);
    }

    public String uploadProfileImage(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Student student = studentRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 1. Check if old image exists & delete it
        if (student.getProfileImageUrl() != null && !student.getProfileImageUrl().isEmpty()) {
            fileService.deleteFile(student.getProfileImageUrl());
        }

        // 2. Upload new image to "profiles" folder
        String newUrl = fileService.saveFile(file, "erp_profiles");

        // 3. Save new URL to DB
        student.setProfileImageUrl(newUrl);
        studentRepository.save(student);

        return newUrl; // Return URL so frontend can show it immediately
    }
}