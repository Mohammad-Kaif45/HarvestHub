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
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("kaifmumtajansari@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Verify Your HarvestHub Account");
        message.setText("Welcome to HarvestHub! \n\n" +
                "Your verification code is: " + otp + "\n\n" +
                "Please enter this code on the website to complete your registration.");

        mailSender.send(message);
        System.out.println("OTP Email sent successfully to " + toEmail);
    }
}