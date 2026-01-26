package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.BranchRequest;
import com.edu.erpbackend.dto.RegisterRequest;
import com.edu.erpbackend.model.Branch;
import com.edu.erpbackend.repository.BranchRepository;
import com.edu.erpbackend.model.Role;
import com.edu.erpbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.edu.erpbackend.model.Subject;
import com.edu.erpbackend.repository.SubjectRepository;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final BranchRepository branchRepository;
    private final SubjectRepository subjectRepository;

    // 🔒 Admin Only: Create Teacher
    @PostMapping("/add-teacher")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addTeacher(@RequestBody RegisterRequest request) {
        request.setRole(Role.TEACHER);
        try {
            authService.register(request);
            return ResponseEntity.ok("Teacher added successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 🔒 Admin Only: Create Student (NEW)
    @PostMapping("/add-student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addStudent(@RequestBody RegisterRequest request) {
        request.setRole(Role.STUDENT); // Force Role
        try {
            authService.register(request);
            return ResponseEntity.ok("Student added successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/add-branch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addBranch(@RequestBody BranchRequest request) {
        // Simple check to prevent duplicates
        if (branchRepository.findByName(request.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Branch '" + request.getName() + "' already exists!");
        }

        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setCode(request.getCode());
        Branch savedBranch = branchRepository.save(branch);

        return ResponseEntity.ok(savedBranch); // Returns the saved branch with ID
    }

    // 2. View All Branches (So you can copy the UUIDs)
    @GetMapping("/branches")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Branch>> getAllBranches() {
        return ResponseEntity.ok(branchRepository.findAll());
    }
    // 3. Create a Subject (e.g., "Data Structures" for CSE Sem 3)
    @PostMapping("/add-subject/{branchId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSubject(@PathVariable java.util.UUID branchId, @RequestBody Subject subject) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        subject.setBranch(branch);
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    // 4. View Subjects (Useful for checking what you created)
    @GetMapping("/subjects/{branchId}/{semester}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Subject>> getSubjects(@PathVariable java.util.UUID branchId, @PathVariable Integer semester) {
        return ResponseEntity.ok(subjectRepository.findByBranchIdAndSemester(branchId, semester));
    }
}