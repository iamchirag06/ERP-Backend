package com.edu.erpbackend.repository.operations;


import com.edu.erpbackend.model.academic.Assignment;
import com.edu.erpbackend.model.operations.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    List<Assignment> findBySubjectId(UUID subjectId);

    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.subject.branch = :branch AND a.subject.semester = :semester")
    long countByBranchAndSemester(@Param("branch") Branch branch, @Param("semester") Integer semester);
}