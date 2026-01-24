package com.edu.erpbackend.repository;


import com.edu.erpbackend.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
    // Find by Employee ID
}