package com.edu.erpbackend.repository;


import com.edu.erpbackend.model.Doubt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DoubtRepository extends JpaRepository<Doubt, UUID> {
    // See all open doubts
    List<Doubt> findByStatus(String status);
}