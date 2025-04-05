package com.example.mobile.service.impl;

import com.example.mobile.model.Notification;
import com.example.mobile.repository.NotificationRepository;
import com.example.mobile.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements INotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
}
