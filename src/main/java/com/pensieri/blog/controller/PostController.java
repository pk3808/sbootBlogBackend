package com.pensieri.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pensieri.blog.model.Post;
import com.pensieri.blog.service.CloudinaryService;
import com.pensieri.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private com.pensieri.blog.service.UserService userService;
    


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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post createPost(@RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                           @RequestParam("postData") String postData) throws Exception {
        // 1. Convert JSON to Object
        ObjectMapper mapper = new ObjectMapper();
        Post post = mapper.readValue(postData, Post.class);

        // 2. Upload Image if exists
        if (coverImage != null && !coverImage.isEmpty()) {
            String url = cloudinaryService.uploadFile(coverImage);
            post.setCoverImage(url);
        }

        // 3. Calculate Read Time
        if (post.getContent() != null) {
            int words = post.getContent().split("\\s+").length;
            post.setReadTime(Math.max(1, words / 200));
        }

        // 4. Set Author from Token (Securely)
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        com.pensieri.blog.model.User user = userService.getUserByEmail(email);
        
        Post.Author authorCtx = new Post.Author();
        authorCtx.setId(user.getId());
        authorCtx.setName(user.getName());
        authorCtx.setEmail(user.getEmail());
        post.setAuthor(authorCtx);

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
