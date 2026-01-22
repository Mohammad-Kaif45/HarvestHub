package com.harvesthub.app.repository;

import com.harvesthub.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom method to find a user by email (used for Login later)
    Optional<User> findByEmail(String email);
}