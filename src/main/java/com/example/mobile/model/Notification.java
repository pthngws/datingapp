package com.example.mobile.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    private String userId;

    private Type type;

    private String content;

    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
