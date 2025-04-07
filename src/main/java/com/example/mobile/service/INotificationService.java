package com.example.mobile.service;

import com.example.mobile.model.Notification;

public interface INotificationService {
    Notification saveNotification(Notification notification);

    void chatAction(String senderId, String receiverId);

    void likeAction(String senderId, String receiverId);

    void matchAction(String senderId, String receiverId);
}
