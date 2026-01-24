package com.edu.erpbackend.repository;


import com.edu.erpbackend.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    // Find all submissions for a specific assignment
    List<Submission> findByAssignmentId(UUID assignmentId);
}