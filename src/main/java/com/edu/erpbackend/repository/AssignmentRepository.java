package com.edu.erpbackend.repository;


import com.edu.erpbackend.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
}