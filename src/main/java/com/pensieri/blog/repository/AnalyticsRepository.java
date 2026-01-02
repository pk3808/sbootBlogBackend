package com.pensieri.blog.repository;
import com.pensieri.blog.model.UserAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface AnalyticsRepository extends MongoRepository<UserAnalytics, String> {
    Optional<UserAnalytics> findByUserId(String userId);
}