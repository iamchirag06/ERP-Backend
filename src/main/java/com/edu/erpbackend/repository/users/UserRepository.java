package com.edu.erpbackend.repository.users;

import com.edu.erpbackend.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // We need this to find a user when they try to log in
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);
}