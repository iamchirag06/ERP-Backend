package com.edu.erpbackend.repository;

import com.edu.erpbackend.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    // Useful to find a student by their Roll Number
    Optional<Student> findByRollNo(String rollNo);

    // ✅ ADD THIS: Find all students in a specific branch and semester
    List<Student> findByBranchIdAndSemester(UUID branchId, Integer semester);
}