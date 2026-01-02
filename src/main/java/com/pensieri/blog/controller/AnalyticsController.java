package com.pensieri.blog.controller;

import com.pensieri.blog.model.UserAnalytics;
import com.pensieri.blog.service.AnalyticsService;
import com.pensieri.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserService userService;

    // GET /api/analytics/me
    @GetMapping("/me")
    public UserAnalytics getMyAnalytics() {
        return analyticsService.getAnalytics(getCurrentUserId());
    }

    // POST /api/analytics/track
    @PostMapping("/track")
    public void trackReading(@RequestBody Map<String, Object> payload) {
        String category = (String) payload.get("category");
        int minutes = (Integer) payload.get("minutes");
        
        analyticsService.trackActivity(getCurrentUserId(), category, minutes);
    }
    
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.getUserByEmail(email).getId();
    }
}
