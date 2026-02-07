package com.edu.erpbackend.controller.users;

import com.edu.erpbackend.dto.TeacherProfileResponse;
import com.edu.erpbackend.model.users.Teacher;
import com.edu.erpbackend.model.users.User;
import com.edu.erpbackend.repository.users.TeacherRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/teacher/profile")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    // ==========================================
    // 1. GET MY PROFILE
    // ==========================================
    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<TeacherProfileResponse> getMyProfile() {
        // 1. Identify User
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        // 2. Fetch Teacher Details
        Teacher teacher = teacherRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));

        // 3. Map to DTO
        return ResponseEntity.ok(TeacherProfileResponse.builder()
                .name(teacher.getName())
                .email(teacher.getEmail())
                .profileImageUrl(teacher.getProfileImageUrl())
                .designation(teacher.getDesignation() != null ? teacher.getDesignation().toString() : "N/A")
                .qualification(teacher.getQualification())
                .branchName(teacher.getBranch() != null ? teacher.getBranch().getName() : "General")
                .joiningDate(teacher.getJoiningDate())
                .phoneNumber(teacher.getPhoneNumber())
                .cabinNumber(teacher.getCabinNumber())
                .build());
    }

    // ==========================================
    // 2. UPDATE PROFILE
    // ==========================================
    @PutMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        Teacher teacher = teacherRepository.findById(user.getId()).orElseThrow();

        // ✅ Allow editing ONLY these fields
        if (updates.containsKey("phoneNumber")) {
            teacher.setPhoneNumber((String) updates.get("phoneNumber"));
        }
        if (updates.containsKey("cabinNumber")) {
            teacher.setCabinNumber((String) updates.get("cabinNumber"));
        }
        if (updates.containsKey("qualification")) {
            teacher.setQualification((String) updates.get("qualification"));
        }
        if (updates.containsKey("profileImageUrl")) {
            teacher.setProfileImageUrl((String) updates.get("profileImageUrl"));
        }

        // ❌ Block editing of Designation, Joining Date, Branch (Admin only)

        teacherRepository.save(teacher);
        return ResponseEntity.ok("Teacher profile updated successfully");
    }
}