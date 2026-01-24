package com.edu.erpbackend.repository;


import com.edu.erpbackend.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    // For Teachers: See all submissions for a specific homework
    List<Submission> findByAssignmentId(UUID assignmentId);

    // For Students: See their own submissions
    List<Submission> findByStudentId(UUID studentId);
}