package com.edu.erpbackend.controller;

import com.edu.erpbackend.model.Doubt;
import com.edu.erpbackend.model.Solution;
import com.edu.erpbackend.service.DoubtService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doubts")
@RequiredArgsConstructor
public class DoubtController {

    private final DoubtService doubtService;

    // Request DTOs
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

    // 1. Ask a Question
    @PostMapping("/ask")
    public ResponseEntity<?> askDoubt(@RequestBody DoubtRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        doubtService.createDoubt(email, request.getSubjectId(), request.getTitle(), request.getDescription(), request.getBountyPoints());
        return ResponseEntity.ok("Doubt posted successfully!");
    }

    // 2. Give an Answer
    @PostMapping("/answer")
    public ResponseEntity<?> giveSolution(@RequestBody SolutionRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        doubtService.addSolution(email, request.getDoubtId(), request.getContent());
        return ResponseEntity.ok("Solution added successfully!");
    }

    // 3. Accept an Answer (Mark as Solved)
    @PostMapping("/accept/{solutionId}")
    public ResponseEntity<?> acceptSolution(@PathVariable UUID solutionId) {
        // In a real app, check if the logged-in user is the 'asker' first!
        doubtService.acceptSolution(solutionId);
        return ResponseEntity.ok("Solution accepted!");
    }

    // 4. View Questions
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Doubt>> getDoubts(@PathVariable UUID subjectId) {
        return ResponseEntity.ok(doubtService.getDoubtsBySubject(subjectId));
    }

    // 5. View Answers
    @GetMapping("/{doubtId}/solutions")
    public ResponseEntity<List<Solution>> getSolutions(@PathVariable UUID doubtId) {
        return ResponseEntity.ok(doubtService.getSolutionsForDoubt(doubtId));
    }
}