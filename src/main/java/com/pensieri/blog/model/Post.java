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
    private int readTime; // In minutes
    private boolean isPremium; // If true, only subscribed users can read
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String authorId;

    private int likeCount;
    private boolean isDeleted;
    private String deletedBy;
    private LocalDateTime deletedAt;
}

