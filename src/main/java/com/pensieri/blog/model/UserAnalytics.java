package com.pensieri.blog.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@Data
@Document(collection = "user_analytics")
public class UserAnalytics {
    @Id
    private String id;
    private String userId;
    private int currentStreak;
    private int maxStreak;
    private LocalDate lastReadDate;
    private int totalReadCount; 
    private Map<String, Integer> dailyReadingMinutes = new HashMap<>();
    private Map<String, Integer> categoryInterests = new HashMap<>();
    private LocalDateTime updatedAt;
}
