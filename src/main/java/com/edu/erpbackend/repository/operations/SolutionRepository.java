package com.edu.erpbackend.repository.operations;

import com.edu.erpbackend.model.academic.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SolutionRepository extends JpaRepository<StudyMaterial.Solution, UUID> {
    // See all solutions for a specific doubt
    List<StudyMaterial.Solution> findByDoubtIdOrderByCreatedAtAsc(UUID doubtId);
}