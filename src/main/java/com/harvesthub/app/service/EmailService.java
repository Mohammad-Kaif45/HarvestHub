package com.harvesthub.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service

public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("your-email@gmail.com"); // Make sure this matches the Username
            message.setTo(to);
            message.setSubject("Your OTP Code");
            message.setText("Your verification code is: " + otp);

            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);

        } catch (Exception e) {
            // THIS is what will show us the real error in Render logs
            System.err.println("!!! EMAIL FAILURE !!!");
            System.err.println("Error Message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}