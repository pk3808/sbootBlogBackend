package com.pensieri.blog.controller;

import com.pensieri.blog.model.User;
import com.pensieri.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 1. Register a new user
    // 1. Register a new user - Delegated to AuthController for OTP flow
    // We keep this but make it safe (no return) or just point users to /api/auth/register
    @PostMapping
    public String registerUser(@RequestBody User user) {
        userService.registerNewUser(user);
        return "Please use /api/auth/register for full OTP flow. User created pending verification.";
    }

    // 2. Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 3. Get user by ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }
    // 4. Update user
    @PutMapping("/{id}")
    public User updateUser(@PathVariable String id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
    // 5. Delete user
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
        // Get currently logged-in user's email from SecurityContext
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // The username in JWT is the email

        userService.changePassword(email, request.getOldPassword(), request.getNewPassword());
        return "Password changed successfully.";
    }

    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
}