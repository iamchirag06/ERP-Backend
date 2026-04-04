package com.edu.erpbackend.controller.users;

import com.edu.erpbackend.dto.StudentProfileResponse;
import com.edu.erpbackend.dto.StudentProfileUpdateRequest;
import com.edu.erpbackend.model.academic.Subject;
import com.edu.erpbackend.model.operations.Branch;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.repository.academic.BranchRepository;
import com.edu.erpbackend.repository.operations.SubjectRepository;
import com.edu.erpbackend.repository.users.StudentRepository;
import com.edu.erpbackend.repository.users.UserRepository;
import com.edu.erpbackend.service.users.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/profile")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;

    // 1. GET MY PROFILE
    @GetMapping
    public ResponseEntity<StudentProfileResponse> getMyProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(studentService.getMyProfile(email));
    }

    // 2. UPDATE PROFILE
    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody StudentProfileUpdateRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        studentService.updateProfile(email, request);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {        
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            String newImageUrl = studentService.uploadProfileImage(email, file);

            // Return the new URL in a JSON object
            return ResponseEntity.ok(Map.of("imageUrl", newImageUrl));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. GET STUDENT'S ASSIGNED BRANCH (ID and Name only)
    @GetMapping("/branches")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyBranch() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Student student = (Student) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        Branch branch = student.getBranch();
        
        if (branch == null) {
            return ResponseEntity.ok("No branch assigned to this student");
        }
        
        Map<String, Object> branchMap = new java.util.LinkedHashMap<>();
        branchMap.put("id", branch.getId().toString());
        branchMap.put("name", branch.getName());
        
        return ResponseEntity.ok(branchMap);
    }

    // 4. GET SUBJECT IDs FOR STUDENT'S BRANCH AND SEMESTER (LIGHT PAYLOAD)
    @GetMapping("/subjects")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMySubjects() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Student student = (Student) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        if (student.getBranch() == null || student.getSemester() == null) {
            return ResponseEntity.ok("Student branch or semester not assigned");
        }
        
        List<Subject> subjects = subjectRepository.findByBranchIdAndSemester(
                student.getBranch().getId(), 
                student.getSemester()
        );
        
        List<Map<String, Object>> subjectList = subjects.stream().map(s -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", s.getId().toString());
            m.put("name", s.getName());
            m.put("code", s.getCode());
            return m;
        }).toList();
        
        return ResponseEntity.ok(subjectList);
    }
}