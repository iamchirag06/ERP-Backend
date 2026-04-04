package com.edu.erpbackend.repository.operations;

import com.edu.erpbackend.model.academic.Solution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SolutionRepository extends JpaRepository<Solution, UUID> {
    // Fetch all answers for a specific doubt, oldest first
    List<Solution> findByDoubtIdOrderByCreatedAtAsc(UUID doubtId);
}