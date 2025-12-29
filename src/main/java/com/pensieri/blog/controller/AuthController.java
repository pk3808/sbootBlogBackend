package com.pensieri.blog.controller;

import com.pensieri.blog.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private com.pensieri.blog.service.UserService userService;

    // 1. Register User (Triggers OTP)
    @PostMapping("/register")
    public String registerUser(@RequestBody com.pensieri.blog.model.User user) {
        try {
            userService.registerNewUser(user);
            return "Registration successful. Please check your email for the verification code.";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 2. Verify OTP
    @PostMapping("/verify")
    public String verifyOtp(@RequestBody VerifyRequest verifyRequest) {
        userService.verifyOtp(verifyRequest.getEmail(), verifyRequest.getOtp());
        return "Email verified successfully! You can now login.";
    }
    
    // 3. Resend OTP
    @PostMapping("/resend")
    public String resendOtp(@RequestParam String email) {
        userService.resendOtp(email);
        return "OTP resent to your email.";
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest loginRequest) {
        // This line authenticates the user using the Database (via CustomUserDetailsService)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // If we get here, valid credentials!
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return response;
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        userService.forgotPassword(email);
        return "Password reset link sent to your email.";
    }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody ResetPasswordRequest request) { // DTO needed
        userService.resetPassword(request.getToken(), request.getNewPassword());
        return "Password successfully reset.";
    }
    // DTOs
    public static class VerifyRequest {
        private String email;
        private String otp;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getOtp() { return otp; }
        public void setOtp(String otp) { this.otp = otp; }
    }

    public static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
