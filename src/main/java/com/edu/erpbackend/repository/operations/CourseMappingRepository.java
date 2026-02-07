package com.edu.erpbackend.repository.operations;


import com.edu.erpbackend.model.academic.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CourseMappingRepository extends JpaRepository<Subject.CourseMapping, UUID> {
    // Find all subjects assigned to a specific teacher
    List<Subject.CourseMapping> findByTeacherId(UUID teacherId);
}