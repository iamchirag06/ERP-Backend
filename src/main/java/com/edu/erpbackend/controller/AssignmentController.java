package com.edu.erpbackend.controller;

import com.edu.erpbackend.dto.AssignmentRequest;
import com.edu.erpbackend.dto.GradeRequest;
import com.edu.erpbackend.dto.SubmissionRequest;
import com.edu.erpbackend.model.Assignment;
import com.edu.erpbackend.model.Submission;
import com.edu.erpbackend.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    // ---------------- TEACHER ENDPOINTS ----------------

    @PostMapping("/create")
    public ResponseEntity<?> createAssignment(@RequestBody AssignmentRequest request) {
        // Get logged-in Teacher's email
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        assignmentService.createAssignment(email, request);
        return ResponseEntity.ok("Assignment created successfully!");
    }

    @PostMapping("/grade")
    public ResponseEntity<?> gradeSubmission(@RequestBody GradeRequest request) {
        assignmentService.gradeSubmission(request);
        return ResponseEntity.ok("Graded successfully!");
    }

    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<List<Submission>> viewSubmissions(@PathVariable UUID assignmentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsForAssignment(assignmentId));
    }

    // ---------------- STUDENT ENDPOINTS ----------------

    @PostMapping("/submit")
    public ResponseEntity<?> submitAssignment(@RequestBody SubmissionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        assignmentService.submitAssignment(email, request);
        return ResponseEntity.ok("Assignment submitted successfully!");
    }

    // ---------------- SHARED ENDPOINTS ----------------

    // Get all assignments for a subject (e.g., View all Java homework)
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Assignment>> getAssignments(@PathVariable UUID subjectId) {
        return ResponseEntity.ok(assignmentService.getAssignmentsBySubject(subjectId));
    }
}