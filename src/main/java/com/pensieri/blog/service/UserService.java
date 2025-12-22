package com.pensieri.blog.service;

import com.pensieri.blog.model.User;
import com.pensieri.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public User registerNewUser(User user) {
        Optional<User> existingUserOpt = userRepository.findByEmail(user.getEmail());

        if(existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (existingUser.isVerified()) {
                throw new RuntimeException("Email already active and verified: " + user.getEmail());
            } else {
                // User exists but verification pending - resend OTP logic
                // Update password just in case they changed it
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                existingUser.setName(user.getName()); // Update name
                return generateAndSendOtp(existingUser);
            }
        }

        // New user
        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setVerified(false); // Default to false
        
        return generateAndSendOtp(user);
    }
    
    private User generateAndSendOtp(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        
        User savedUser = userRepository.save(user);
        
        // Send Email
        emailService.sendOtpEmail(user.getEmail(), otp);
        
        return savedUser;
    }

    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.isVerified()) {
            return true; // Already verified
        }

        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP Expired");
        }

        if (user.getOtp().equals(otp)) {
            user.setVerified(true);
            user.setOtp(null);
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        } else {
             throw new RuntimeException("Invalid OTP");
        }
    }

    public void resendOtp(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if(user.isVerified()) {
             throw new RuntimeException("User already verified");
        }
        generateAndSendOtp(user);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 4. Update user
    public User updateUser(String id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id); // Ensure ID matches
            user.setUpdatedAt(LocalDateTime.now());
            // In a real app, we would copy specific fields to avoid overwriting everything
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found: " + id);
    }

    // 5. Delete user
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
