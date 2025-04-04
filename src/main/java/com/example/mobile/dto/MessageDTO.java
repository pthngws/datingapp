package com.example.mobile.dto;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class MessageDTO {
    private ObjectId senderId;
    private ObjectId receiverId;
    private String content;
}