package com.edu.erpbackend.repository.operations;

import com.edu.erpbackend.model.academic.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    // Existing methods
    List<Submission> findByAssignmentId(UUID assignmentId);
    List<Submission> findByStudentId(UUID studentId);
    Optional<Submission> findByAssignmentIdAndStudentId(UUID assignmentId, UUID studentId);

    // ✅ FIX 1: For Teacher Dashboard (Count Ungraded)
    // "Assignment_" tells JPA to look inside the Assignment entity -> find Teacher -> find ID
    long countByAssignment_TeacherIdAndGradeIsNull(UUID teacherId);

    // ✅ FIX 2: For Student Dashboard (Count My Submissions)
    long countByStudentId(UUID studentId);
}