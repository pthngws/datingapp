package com.example.mobile.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reports")
public class Report {
    @Id
    private ObjectId id;

    private ObjectId reporterId;

    private ObjectId reportedUserId;

    private String reason;

    private LocalDateTime createdAt = LocalDateTime.now();
}
