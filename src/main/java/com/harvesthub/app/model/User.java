package com.harvesthub.app.model;

import jakarta.persistence.*;
import lombok.Data; // Removes need for Getters/Setters

@Entity
@Data
@Table(name = "users") // Renaming table to 'users' because 'user' is a reserved keyword in SQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    // We will store roles as simple strings: "FARMER", "RETAIL", "WHOLESALE"
    @Column(nullable = false)
    private String role;
}