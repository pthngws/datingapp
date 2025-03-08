package com.example.mobile.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reports")
public class Report {
    @Id
    private String id;

    private String reporterId;

    private String reportedUserId;

    private String reason;

    private LocalDateTime createdAt = LocalDateTime.now();
}
