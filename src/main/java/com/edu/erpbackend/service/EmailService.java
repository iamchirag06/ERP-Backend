package com.edu.erpbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("chiragsingh.cse23@satyug.edu.in"); // Set your sender email here

            mailSender.send(message);
            System.out.println("✅ Email sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
            // Don't throw exception here to avoid crashing the user request
        }
    }
}