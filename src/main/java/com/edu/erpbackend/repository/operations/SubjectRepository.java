package com.edu.erpbackend.repository.operations;

import com.edu.erpbackend.model.academic.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {

    // Find all subjects for a branch (e.g., All CSE subjects)
    List<Subject> findByBranchId(UUID branchId);

    // 🆕 Find subjects for a specific Branch AND Semester (e.g., CSE Sem 3)
    List<Subject> findByBranchIdAndSemester(UUID branchId, Integer semester);
}