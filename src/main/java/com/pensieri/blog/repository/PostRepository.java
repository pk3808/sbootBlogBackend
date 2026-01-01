package com.pensieri.blog.repository;

import com.pensieri.blog.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.pensieri.blog.dto.CategoryStats;
import org.springframework.data.mongodb.repository.Aggregation;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    // Custom Aggregation: Group by Category, Count, Filter out Premium
    // 'isPremium': false
    // 'status': 'PUBLISHED' (Assuming we only want published ones too?)
    // The user didn't explicitly say "published only", but it implies "visible" posts. 
    // Let's stick to "isPremium: false" as requested.
    
    @Aggregation(pipeline = {
        "{ '$match': { 'isPremium': false, 'status': 'PUBLISHED', 'isDeleted': false } }",
        "{ '$group': { '_id': '$category', 'count': { '$sum': 1 } } }",
        "{ '$project': { 'category': '$_id', 'count': 1, '_id': 0 } }"
    })
    List<CategoryStats> getCategoryStats();
    
    List<Post> findByAuthorId(String authorId);

    // 1. Fresh Blogs (Latest first)
    // Spring Data Magic: "OrderBy" + "CreatedAt" + "Desc"
    List<Post> findAllByOrderByCreatedAtDesc();

    // 2. Trending (Most Liked first)
    // Spring Data Magic: "OrderBy" + "LikeCount" + "Desc"
    List<Post> findAllByOrderByLikeCountDesc();

    // 3. Premium Posts
    List<Post> findByIsPremiumTrue();

    // 4. By Category
    // 4. By Category - Only Published & Non-Deleted
    List<Post> findByCategoryAndStatusAndIsDeletedFalse(String category, com.pensieri.blog.model.PostStatus status);

    // 5. Search by Title - Only Published & Non-Deleted
    List<Post> findByTitleContainingIgnoreCaseAndStatusAndIsDeletedFalse(String title, com.pensieri.blog.model.PostStatus status);
}