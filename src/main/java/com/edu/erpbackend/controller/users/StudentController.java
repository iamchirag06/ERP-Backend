package com.edu.erpbackend.controller.users;

import com.edu.erpbackend.dto.StudentProfileResponse;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/profile")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    // 1. GET MY PROFILE
    @GetMapping
    public ResponseEntity<StudentProfileResponse> getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Student student = studentRepository.findById(user.getId()).orElseThrow();

        return ResponseEntity.ok(StudentProfileResponse.builder()
                .name(student.getName())
                .email(student.getEmail())
                .rollNo(student.getRollNo())
                .profileImageUrl(student.getProfileImageUrl())
                .branchName(student.getBranch() != null ? student.getBranch().getName() : "Unknown")
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
                .build());
    }

    // 2. UPDATE PROFILE
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Student student = studentRepository.findById(user.getId()).orElseThrow();

        // ✅ Allow editing ONLY these fields
        if (updates.containsKey("phoneNumber")) student.setPhoneNumber((String) updates.get("phoneNumber"));
        if (updates.containsKey("linkedinProfile")) student.setLinkedinProfile((String) updates.get("linkedinProfile"));
        if (updates.containsKey("githubProfile")) student.setGithubProfile((String) updates.get("githubProfile"));
        if (updates.containsKey("address")) student.setAddress((String) updates.get("address"));

        // Handle Skills List
        if (updates.containsKey("skills")) {
            student.setSkills((List<String>) updates.get("skills"));
        }

        // Handle Profile Image (If the URL is sent from frontend after upload)
        if (updates.containsKey("profileImageUrl")) {
            student.setProfileImageUrl((String) updates.get("profileImageUrl"));
        }

        studentRepository.save(student);
        return ResponseEntity.ok("Profile updated successfully");
    }
}