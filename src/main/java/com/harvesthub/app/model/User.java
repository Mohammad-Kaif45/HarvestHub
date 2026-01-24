package com.harvesthub.app.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
public class User implements UserDetails { // <--- 1. Implements UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String role;

    private String otp;

    private boolean verified; // defaults to false automatically

    // --- STANDARD GETTERS AND SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }


    // --- SPRING SECURITY MANDATORY METHODS ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // This converts your String role into a standard Spring Authority
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getUsername() {
        return this.email; // We use email as the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Account never expires
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Account is never locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Password never expires
    }

    // --- 🚨 THIS IS THE IMPORTANT PART 🚨 ---
    @Override
    public boolean isEnabled() {
        return this.verified; // If verified is false, LOGIN WILL FAIL AUTOMATICALLY!
    }
}