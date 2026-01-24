package com.edu.erpbackend.controller;

import com.edu.erpbackend.model.Branch;
import com.edu.erpbackend.model.Student;
import com.edu.erpbackend.model.Subject;
import com.edu.erpbackend.repository.BranchRepository;
import com.edu.erpbackend.repository.StudentRepository; // Add this
import com.edu.erpbackend.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class CollegeStructureController {

    private final BranchRepository branchRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository; // Add this

    // 1. Create a Branch
    @PostMapping("/branches")
    public ResponseEntity<?> createBranch(@RequestBody Branch branch) {
        return ResponseEntity.ok(branchRepository.save(branch));
    }

    // 2. Create a Subject
    @PostMapping("/subjects/{branchId}")
    public ResponseEntity<?> createSubject(@PathVariable UUID branchId, @RequestBody Subject subject) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        subject.setBranch(branch);
        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    // 3. Helper: Link a Student to a Branch (Crucial for Attendance!)
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
}