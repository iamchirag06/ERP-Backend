package com.edu.erpbackend.controller.academic;

import com.edu.erpbackend.model.academic.Doubt;
import com.edu.erpbackend.model.academic.Solution;
import com.edu.erpbackend.model.users.Student;
import com.edu.erpbackend.repository.users.UserRepository;
import com.edu.erpbackend.service.academic.DoubtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/doubts")
@RequiredArgsConstructor
public class DoubtController {

    private final DoubtService doubtService;
    private final UserRepository userRepository;

    @Data static class DoubtRequest {
        private UUID subjectId;
        private String title;
        private String description;
        private Integer bountyPoints;
    }

    @Data static class SolutionRequest {
        private UUID doubtId;
        private String content;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> askDoubt(@RequestBody DoubtRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        doubtService.createDoubt(email, request.getSubjectId(), request.getTitle(), request.getDescription(), request.getBountyPoints());
        return ResponseEntity.ok("Doubt posted successfully!");
    }

    @PostMapping("/answer")
    public ResponseEntity<?> giveSolution(@RequestBody SolutionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        doubtService.addSolution(email, request.getDoubtId(), request.getContent());
        return ResponseEntity.ok("Solution added successfully!");
    }

    // ✅ FIXED: Now passes email for security verification
    @PostMapping("/accept/{solutionId}")
    public ResponseEntity<?> acceptSolution(@PathVariable UUID solutionId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        doubtService.acceptSolution(email, solutionId);
        return ResponseEntity.ok("Solution accepted!");
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Doubt>> getDoubts(@PathVariable UUID subjectId) {
        return ResponseEntity.ok(doubtService.getDoubtsBySubject(subjectId));
    }

    @GetMapping("/{doubtId}/solutions")
    public ResponseEntity<List<Solution>> getSolutions(@PathVariable UUID doubtId) {
        return ResponseEntity.ok(doubtService.getSolutionsForDoubt(doubtId));
    }

    // ✅ NEW ENDPOINT 1: Get all doubts across all subjects (FIXED: /all to avoid conflict)
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getAllDoubts() {
        try {
            List<Doubt> doubts = doubtService.getAllDoubts();
            List<Map<String, Object>> doubtList = doubtService.formatDoubtsResponse(doubts, false);
            return ResponseEntity.ok(doubtList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch doubts: " + e.getMessage()));
        }
    }

    // ✅ NEW ENDPOINT 2: Get all doubts for student's subjects (by branch and semester)
    @GetMapping("/my-subjects")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getDoubtsForMySubjects() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Doubt> doubts = doubtService.getDoubtsForStudentSubjects(email);
            List<Map<String, Object>> doubtList = doubtService.formatDoubtsResponse(doubts, false);
            return ResponseEntity.ok(doubtList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", "Student not found or branch not assigned"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch doubts: " + e.getMessage()));
        }
    }

    // ✅ NEW ENDPOINT 3: Get all doubts posted by a specific student
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getDoubtsByStudent(@PathVariable UUID studentId) {
        try {
            List<Doubt> doubts = doubtService.getDoubtsByStudent(studentId);
            List<Map<String, Object>> doubtList = doubtService.formatDoubtsResponse(doubts, true);
            return ResponseEntity.ok(doubtList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch student doubts: " + e.getMessage()));
        }
    }

    // ✅ NEW ENDPOINT 4: Get all solutions for a specific student's doubts
    @GetMapping("/solutions/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<?> getSolutionsForStudent(@PathVariable UUID studentId) {
        try {
            List<Map<String, Object>> solutions = doubtService.getSolutionsForStudent(studentId);
            return ResponseEntity.ok(solutions);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch solutions: " + e.getMessage()));
        }
    }

    // ✅ NEW ENDPOINT 5: Get all solutions for current logged-in student
    @GetMapping("/solutions/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> getMyDoubtsWithSolutions() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Map<String, Object>> solutions = doubtService.getMyDoubtsWithSolutions(email);
            return ResponseEntity.ok(solutions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", "Student not found"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch solutions: " + e.getMessage()));
        }
    }
}