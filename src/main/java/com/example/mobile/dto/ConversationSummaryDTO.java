package com.example.mobile.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationSummaryDTO {
    private String userId;
    private String name; // FirstName + LastName from Profile
    private String profilePicture; // pic1 from Album
    private String latestMessage; // Content of the most recent message
    private LocalDateTime latestMessageTime; // Send time of the latest message
    private int unreadCount; // Number of unread messages sent by this user
}