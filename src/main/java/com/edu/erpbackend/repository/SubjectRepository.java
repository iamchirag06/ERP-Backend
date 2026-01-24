package com.edu.erpbackend.repository;


import com.edu.erpbackend.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
}