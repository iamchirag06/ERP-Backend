package com.edu.erpbackend.repository;


import com.edu.erpbackend.model.CourseMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CourseMappingRepository extends JpaRepository<CourseMapping, UUID> {
    // Find all subjects assigned to a specific teacher
    List<CourseMapping> findByTeacherId(UUID teacherId);
}