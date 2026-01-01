package com.pensieri.blog.controller;

import com.pensieri.blog.dto.CategoryStats;
import com.pensieri.blog.model.Post;
import com.pensieri.blog.model.User;
import com.pensieri.blog.service.PostService;
import com.pensieri.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    // 1. Defaul Search Dropdown Data (Trending & Topics)
    @GetMapping("/defaults")
    public Map<String, Object> getSearchDefaults() {
        Map<String, Object> response = new HashMap<>();
        
        // Trending: Top 5 Posts
        List<Post> trending = postService.getTrendingPosts().stream().limit(5).collect(Collectors.toList());
        response.put("trendingPosts", trending);

        // Topics: Top 8 Categories (Most populated)
        List<CategoryStats> categories = postService.getCategoryStats().stream()
            .sorted((a, b) -> Long.compare(b.getCount(), a.getCount())) // Sort desc
            .limit(8)
            .collect(Collectors.toList());
        
        response.put("popularCategories", categories);
        
        return response;
    }

    // 2. Main Search Endpoint
    @GetMapping
    public Map<String, Object> search(@RequestParam String q) {
        Map<String, Object> response = new HashMap<>();
        
        if (q == null || q.trim().isEmpty()) {
            return getSearchDefaults(); // Fallback
        }

        // Search Posts (Title)
        List<Post> posts = postService.searchPosts(q);
        response.put("posts", posts);

        // Search Users (Name)
        List<User> users = userService.searchUsers(q);
        response.put("users", users);

        // Search Topics (Filter Categories)
        List<CategoryStats> categories = postService.getCategoryStats().stream()
            .filter(c -> c.getCategory().toLowerCase().contains(q.toLowerCase()))
            .collect(Collectors.toList());
        response.put("categories", categories);

        return response;
    }
}
