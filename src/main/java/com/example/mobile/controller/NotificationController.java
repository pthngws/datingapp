package com.example.mobile.controller;

import com.example.mobile.model.Notification;
import com.example.mobile.repository.NotificationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping("/user/{userId}")
    public List<Map<String, Object>> getNotificationsByUserId(@PathVariable String userId) {
        List<Notification> notifications = notificationRepository.findByUserId(new ObjectId(userId));
        return notifications.stream().map(n -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getId().toHexString());
            map.put("userId", n.getUserId().toHexString());
            map.put("type", n.getType().toString());
            map.put("content", n.getContent());
            map.put("isRead", n.isRead());
            map.put("createdAt", n.getCreatedAt().toString());
            return map;
        }).collect(Collectors.toList());
    }

    // Xử lý thông báo gửi qua WebSocket
    @MessageMapping("/notify")
    public void sendNotification(Notification notification) {
        System.out.println("Received notification in NotificationController: " + notification);

        // Lưu thông báo vào database
        notification.setCreatedAt(LocalDateTime.now());
        Notification savedNotification = notificationRepository.save(notification);

        // Gửi thông báo đến topic chung
        template.convertAndSend("/topic/notifications", savedNotification);
    }
}