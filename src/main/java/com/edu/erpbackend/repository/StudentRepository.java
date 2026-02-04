package com.edu.erpbackend.repository;

import com.edu.erpbackend.model.Branch;
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
    List<Student> findByBranchAndSemester(Branch branch, Integer semester);

    // ✅ Find all students of a specific batch (e.g., "Give me all 2023-2027 students")
    List<Student> findByBatch(String batch);

    // ✅ Advanced: Find batch students in a specific branch (e.g., "CSE 2023-2027")
    List<Student> findByBatchAndBranchId(String batch, UUID branchId);
}