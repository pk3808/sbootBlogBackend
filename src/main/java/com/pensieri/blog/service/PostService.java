package com.pensieri.blog.service;

import com.pensieri.blog.model.Post;
import com.pensieri.blog.model.PostStatus;
import com.pensieri.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        if (post.getStatus() == null) {
            post.setStatus(PostStatus.DRAFT);
        }
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }

    // --- Feed Methods ---

    // 1. Fresh / Latest
    public List<Post> getFreshPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    // 2. Trending
    public List<Post> getTrendingPosts() {
        return postRepository.findAllByOrderByLikeCountDesc();
    }

    // 3. Premium
    public List<Post> getPremiumPosts() {
        return postRepository.findByIsPremiumTrue();
    }

    // 4. Category
    public List<Post> getPostsByCategory(String category) {
        return postRepository.findByCategory(category);
    }

    // --------------------

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post updatePost(Post post) {
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    public void deletePost(String id) {
        postRepository.deleteById(id);
    }
}