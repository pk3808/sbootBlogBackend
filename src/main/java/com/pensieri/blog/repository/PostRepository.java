package com.pensieri.blog.repository;

import com.pensieri.blog.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
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
    List<Post> findByCategory(String category);
}