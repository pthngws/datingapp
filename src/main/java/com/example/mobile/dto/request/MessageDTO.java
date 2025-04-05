package com.example.mobile.dto.request;

import lombok.Data;

@Data
public class MessageDTO {
    private String senderId; // Chuỗi hex của ObjectId
    private String receiverId; // Chuỗi hex của ObjectId
    private String content;
}