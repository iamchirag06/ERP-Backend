package com.edu.erpbackend.repository;

import com.edu.erpbackend.model.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SolutionRepository extends JpaRepository<Solution, UUID> {
    // See all solutions for a specific doubt
    List<Solution> findByDoubtIdOrderByCreatedAtAsc(UUID doubtId);
}