package com.edu.erpbackend.repository;

import com.edu.erpbackend.model.Doubt;
import com.edu.erpbackend.model.DoubtStatus; // ✅ Import the Enum
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DoubtRepository extends JpaRepository<Doubt, UUID> {

    // ✅ Corrected: Use 'DoubtStatus' instead of 'String'
    List<Doubt> findByStatus(DoubtStatus status);

    // This one looks perfect (assuming you added 'subject' and 'createdAt' to your Model)
    List<Doubt> findBySubjectIdOrderByCreatedAtDesc(UUID subjectId);
}