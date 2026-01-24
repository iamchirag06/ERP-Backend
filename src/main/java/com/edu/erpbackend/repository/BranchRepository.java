package com.edu.erpbackend.repository;

import com.edu.erpbackend.model.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
}