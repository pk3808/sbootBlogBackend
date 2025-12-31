package com.pensieri.blog.service;

import com.pensieri.blog.model.User;
import com.pensieri.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.pensieri.blog.model.AuthProvider;
import com.pensieri.blog.security.JwtUtils;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;
@Autowired
    private FirebaseAuth firebaseAuth;
    
    @Autowired
    private com.pensieri.blog.security.JwtUtils jwtUtils;
    public User registerNewUser(User user) {
        Optional<User> existingUserOpt = userRepository.findByEmail(user.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (existingUser.isVerified()) {
                throw new RuntimeException("Email already active and verified: " + user.getEmail());
            } else {
                // User exists but verification pending
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                existingUser.setName(user.getName()); // Update name
                return generateAndSendOtp(existingUser);
            }
        }

        // New user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setVerified(false);
        
        return generateAndSendOtp(user);
    }
    
    private User generateAndSendOtp(User user) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        
        User savedUser = userRepository.save(user); // Save first to get ID/Update
        
        // Send Email
        emailService.sendOtpEmail(user.getEmail(), otp);
        
        return savedUser;
    }

    public boolean verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.isVerified()) {
            return true;
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
        
        if (user.isVerified()) {
             throw new RuntimeException("User already verified");
        }
        generateAndSendOtp(user);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);
        
        emailService.sendResetEmail(user.getEmail(), token);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid Token"));
            
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token Expired");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(String id, User user) {
        if (userRepository.existsById(id)) {
            user.setId(id);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found: " + id);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public String googleLogin(String idToken) {
        try {
            // 1. Verify Google Token
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String picture = decodedToken.getPicture(); // Extract picture URL

            // 2. Check existence
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;

            if (userOpt.isPresent()) {
                user = userOpt.get();
                // Optional: Update profile image if they haven't set a custom one? 
                // Or always update it? Let's assume we update it if it's currently null.
                if (user.getProfileImage() == null && picture != null) {
                        user.setProfileImage(picture);
                        userRepository.save(user);
                }
            } else {
                // 3. Register New User
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setProfileImage(picture); // Save Google Image
                user.setAuthProvider(AuthProvider.GOOGLE);
                user.setVerified(true);
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                user.setPassword(""); 
                
                // Initialize empty social profiles if you want (optional)
                user.setSocialProfiles(new User.SocialProfiles());
                
                userRepository.save(user);
            }

            // 4. Generate Token (Using dummy UserDetails)
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("")
                .authorities("USER")
                .build();
                
            return jwtUtils.generateToken(userDetails);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Google Login Failed", e);
        }
    }
}
