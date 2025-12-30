package com.pensieri.blog.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Document(collection = "blogs")
public class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private List<String> tags;
    private String category;
    private String coverImage; // URL from Cloudinary
    private int readTime; // In minutes
    @com.fasterxml.jackson.annotation.JsonProperty("isPremium")
    private boolean isPremium; // If true, only subscribed users can read
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Author author;

    @Data
    public static class Author {
        private String id;
        private String name;
        private String email;
    }

    private int likeCount;
    @com.fasterxml.jackson.annotation.JsonProperty("isDeleted")
    private boolean isDeleted;
    private String deletedBy;
    private LocalDateTime deletedAt;
}

