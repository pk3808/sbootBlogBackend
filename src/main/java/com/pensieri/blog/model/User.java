package com.pensieri.blog.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

// @Data: This is magic from Lombok to create getters, setters, toString, etc.
// @Document: Tells Spring "This class = 'users' collection in MongoDB"
@Data
@Document(collection = "users")
public class User {

    @Id // The primary key (_id)
    private String id;

    private String name;
    private String email;
    private String password; 
    
    // --- NEW PROFILE FIELDS ---
    private String profileImage;
    private String bio;
    private String location;
    
    // Inner class for Social Profiles
    private SocialProfiles socialProfiles;
    // --------------------------    
    private String otp;
    private LocalDateTime otpExpiry;
    private boolean isVerified; 

    private AuthProvider authProvider; // Enum: LOCAL or GOOGLE
    private Role role; 

    private boolean isActive;
    private boolean isSubscribed; // True if user has paid membership

    private Restrictions restrictions;
    private BlogQuota blogQuota;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    // Simple inner classes to group related fields (like in your JSON)
    @Data
    public static class Restrictions {
        private CommentingRestriction commenting;
    }

    @Data
    public static class CommentingRestriction {
        private boolean isBlocked;
        private String reason;
        private LocalDateTime blockedUntil;
    }

    @Data
    public static class BlogQuota {
        private int monthlyLimit;
        private int usedThisMonth;
    }

    // --- NEW INNER CLASS ---
    @Data
    public static class SocialProfiles {
        private String instagram;
        private String twitter;
        private String linkedin;
        private String facebook;
    }
}
