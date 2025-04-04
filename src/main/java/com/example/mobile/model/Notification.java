package com.example.mobile.model;

import com.example.mobile.model.enums.Type;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private ObjectId id;

    private ObjectId userId;

    private Type type;

    private String content;

    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
