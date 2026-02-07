package com.edu.erpbackend.repository.operations;

import com.edu.erpbackend.model.academic.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, UUID> {

    // 1. Get all notes for a specific Subject (e.g., "Java Notes")
    List<StudyMaterial> findBySubjectIdOrderByUploadedAtDesc(UUID subjectId);

    // 2. Filter by Unit (e.g., "Show me Unit 1 notes for Java")
    List<StudyMaterial> findBySubjectIdAndUnitTag(UUID subjectId, String unitTag);
}