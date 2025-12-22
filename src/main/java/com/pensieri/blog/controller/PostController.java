package com.pensieri.blog.controller;

import com.pensieri.blog.model.Post;
import com.pensieri.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // --- Feed Endpoints ---

    // GET /api/posts/fresh
    @GetMapping("/fresh")
    public List<Post> getFreshPosts() {
        return postService.getFreshPosts();
    }

    // GET /api/posts/trending
    @GetMapping("/trending")
    public List<Post> getTrendingPosts() {
        return postService.getTrendingPosts();
    }

    // GET /api/posts/premium
    @GetMapping("/premium")
    public List<Post> getPremiumPosts() {
        return postService.getPremiumPosts();
    }

    // GET /api/posts/category/Technology
    @GetMapping("/category/{category}")
    public List<Post> getPostsByCategory(@PathVariable String category) {
        return postService.getPostsByCategory(category);
    }

    // ----------------------

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public Optional<Post> getPostById(@PathVariable String id) {
        return postService.getPostById(id);
    }

    @PutMapping
    public Post updatePost(@RequestBody Post post) {
        return postService.updatePost(post);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable String id) {
        postService.deletePost(id);
    }
}
