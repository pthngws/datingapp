package com.example.mobile.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private ObjectId id;

    private String content;

    private String senderId;

    private String receiverId;

    private boolean isRead = false;

    private LocalDateTime sendTime = LocalDateTime.now();
}
