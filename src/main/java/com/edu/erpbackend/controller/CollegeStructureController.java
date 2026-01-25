package com.edu.erpbackend.controller;

import com.edu.erpbackend.model.Branch;
import com.edu.erpbackend.model.Student;
import com.edu.erpbackend.model.Subject;
import com.edu.erpbackend.repository.BranchRepository;
import com.edu.erpbackend.repository.StudentRepository;
import com.edu.erpbackend.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER')") // Only Teachers/Admins can access this
public class CollegeStructureController {

    private final BranchRepository branchRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;

    // 1. Create a Branch (With Duplicate Check)
    @PostMapping("/branches")
    public ResponseEntity<?> createBranch(@RequestBody Branch branch) {
        // ✅ Check if branch code already exists
        if (branchRepository.existsByCode(branch.getCode())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT) // Returns 409 Conflict
                    .body("Error: A branch with code '" + branch.getCode() + "' already exists.");
        }
        return ResponseEntity.ok(branchRepository.save(branch));
    }

    // 🆕 Feature: Get All Branches
    @GetMapping("/branches")
    public ResponseEntity<List<Branch>> getAllBranches() {
        return ResponseEntity.ok(branchRepository.findAll());
    }

    // 2. Create a Subject
    @PostMapping("/subjects/{branchId}")
    public ResponseEntity<?> createSubject(@PathVariable UUID branchId, @RequestBody Subject subject) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        subject.setBranch(branch);
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    // 3. Helper: Link a Student to a Branch
    @PostMapping("/assign-branch")
    public ResponseEntity<?> assignBranch(@RequestParam UUID studentId, @RequestParam UUID branchId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        student.setBranch(branch);
        studentRepository.save(student);
        return ResponseEntity.ok("Student assigned to branch successfully!");
    }

    // 4. Get Subjects by Semester
    @GetMapping("/subjects/branch/{branchId}/semester/{semester}")
    public ResponseEntity<List<Subject>> getSubjectsBySemester(
            @PathVariable UUID branchId,
            @PathVariable Integer semester) {
        return ResponseEntity.ok(subjectRepository.findByBranchIdAndSemester(branchId, semester));
    }

    // 5. Update a Subject's Semester
    @PutMapping("/subjects/{subjectId}")
    public ResponseEntity<?> updateSubjectSemester(
            @PathVariable UUID subjectId,
            @RequestParam Integer newSemester) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        subject.setSemester(newSemester);
        return ResponseEntity.ok(subjectRepository.save(subject));
    }
}