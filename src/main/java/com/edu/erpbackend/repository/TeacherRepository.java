package com.edu.erpbackend.repository;


import com.edu.erpbackend.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    List<Teacher> findByDepartment(String department);

    // "Find the teachers associated with subjects in this branch and semester"
    @Query("SELECT DISTINCT s.teacher FROM Subject s WHERE s.branch.id = :branchId AND s.semester = :semester")
    List<Teacher> findByBranchAndSemester(@Param("branchId") UUID branchId, @Param("semester") Integer semester);
}