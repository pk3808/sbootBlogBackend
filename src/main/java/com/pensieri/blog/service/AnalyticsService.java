package com.pensieri.blog.service;
import com.pensieri.blog.model.UserAnalytics;
import com.pensieri.blog.repository.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
@Service
public class AnalyticsService {
    @Autowired
    private AnalyticsRepository analyticsRepository;
    public UserAnalytics getAnalytics(String userId) {
        return analyticsRepository.findByUserId(userId)
                .orElseGet(() -> createNewAnalytics(userId));
    }
    public void trackActivity(String userId, String category, int minutesSpent) {
        UserAnalytics analytics = getAnalytics(userId);
        LocalDate today = LocalDate.now();
        // 1. Streak Logic
        if (analytics.getLastReadDate() != null) {
            LocalDate yesterday = today.minusDays(1);
            if (analytics.getLastReadDate().equals(yesterday)) {
                analytics.setCurrentStreak(analytics.getCurrentStreak() + 1);
            } else if (analytics.getLastReadDate().isBefore(yesterday)) {
                analytics.setCurrentStreak(1);
            }
        } else {
            analytics.setCurrentStreak(1);
        }
        if (analytics.getCurrentStreak() > analytics.getMaxStreak()) {
            analytics.setMaxStreak(analytics.getCurrentStreak());
        }
        analytics.setLastReadDate(today);
        // 2. Daily Minutes
        analytics.getDailyReadingMinutes().merge(today.toString(), minutesSpent, Integer::sum);
        // 3. Categories
        if (category != null && !category.isEmpty()) {
            analytics.getCategoryInterests().merge(category, 1, Integer::sum);
        }
        analytics.setTotalReadCount(analytics.getTotalReadCount() + 1);
        analytics.setUpdatedAt(LocalDateTime.now());
        analyticsRepository.save(analytics);
    }
    private UserAnalytics createNewAnalytics(String userId) {
        UserAnalytics ua = new UserAnalytics();
        ua.setUserId(userId);
        ua.setCurrentStreak(0);
        ua.setMaxStreak(0);
        ua.setTotalReadCount(0);
        ua.setUpdatedAt(LocalDateTime.now());
        return analyticsRepository.save(ua);
    }
}