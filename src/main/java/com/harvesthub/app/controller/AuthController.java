package com.harvesthub.app.controller;

import com.harvesthub.app.model.User;
import com.harvesthub.app.repository.UserRepository;
import com.harvesthub.app.service.EmailService;
import jakarta.servlet.http.HttpSession; // Needed for Session
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Random;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService; // 1. Inject EmailService

    // 2. Update Constructor to include EmailService
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register/save")
    public String saveUser(@ModelAttribute User user, HttpSession session, Model model) {

        // A. Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email is already registered!");
            return "register";
        }

        // B. Hash the password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // --- FIX: Add "ROLE_" Prefix for Spring Security ---
        // If the form sends "FARMER", this changes it to "ROLE_FARMER"
        if (user.getRole() != null && !user.getRole().startsWith("ROLE_")) {
            user.setRole("ROLE_" + user.getRole());
        }

        // C. Generate 6-Digit OTP
        String randomOtp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(randomOtp);
        user.setVerified(false); // Mark as NOT verified yet

        // D. Save User to Database
        userRepository.save(user);

        // E. Send Email
        // If this fails, the user is saved but won't get the OTP.
        // We will fix the timeout in Step 2 below.
        try {
            emailService.sendOtpEmail(user.getEmail(), randomOtp);
        } catch (Exception e) {
            e.printStackTrace();
            // Optional: You could delete the user here if email fails,
            // so they can try registering again.
        }

        // F. Store email in session temporarily
        session.setAttribute("temp_email", user.getEmail());

        // G. Redirect to OTP Page
        return "redirect:/verify-otp";
    }

    // --- NEW: OTP Verification Methods ---

    @GetMapping("/verify-otp")
    public String showOtpPage() {
        return "verify-otp"; // This must match your verify-otp.html filename
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String enteredOtp, HttpSession session, Model model) {
        // Get the email of the user who is currently registering
        String email = (String) session.getAttribute("temp_email");

        if (email == null) {
            return "redirect:/register"; // Session expired, start over
        }

        // Add .orElse(null) to the end
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null && user.getOtp().equals(enteredOtp)) {
            // SUCCESS: OTP matches!
            user.setVerified(true);
            user.setOtp(null); // Clear OTP so it can't be reused
            userRepository.save(user);

            session.removeAttribute("temp_email"); // Clean up session

            return "redirect:/login?verified=true"; // Send to login
        } else {
            // FAILURE: Wrong OTP
            model.addAttribute("error", "Invalid OTP. Please check your email and try again.");
            return "verify-otp"; // Reload the page with error
        }
    }
}