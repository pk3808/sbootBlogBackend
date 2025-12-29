package com.pensieri.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("thepensieri@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Your Pensieri Verification Code"); // Branding update

            // Load HTML template
            String htmlContent = loadHtmlTemplate(otp);
            helper.setText(htmlContent, true); // true = HTML

            // Add Inline Image (Logo)
            // Note: In production you might want to use a hosted URL, but inline works for now
            org.springframework.core.io.ClassPathResource imageResource = new org.springframework.core.io.ClassPathResource("static/inkwise.png");
            if (imageResource.exists()) {
                helper.addInline("logoImage", imageResource);
            }

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send email");
        }
    }

    public void sendResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("thepensieri@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Reset Your Password - Pensieri");
            message.setText("To reset your password, click the link below:\n\n" +
                    "http://localhost:3000/reset-password?token=" + token + "\n\n" +
                    "This link expires in 15 minutes.");
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send reset email");
        }
    }

    private String loadHtmlTemplate(String otp) {
        try {
            org.springframework.core.io.ClassPathResource resource = new org.springframework.core.io.ClassPathResource("templates/otp-email.html");
             java.nio.file.Path path = java.nio.file.Paths.get(resource.getURI());
            String content = java.nio.file.Files.readString(path);
            
            // Simple placeholder replacement
            return content.replace("{{otp}}", otp);
            
        } catch (Exception e) {
            // Fallback if template fails loading
            return "<h1>Your OTP is: " + otp + "</h1>";
        }
    }
}
