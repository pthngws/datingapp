package com.example.mobile.controller;

import com.example.mobile.model.Notification;
import com.example.mobile.model.enums.Type;
import com.example.mobile.service.INotificationService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(Notification notification) {
        Notification savedNotification = notificationService.saveNotification(notification);
        System.out.println("Sending notification to user: " + savedNotification.getUserId());

        // Gửi trực tiếp `savedNotification`
        messagingTemplate.convertAndSendToUser(
                savedNotification.getUserId().toHexString(),
                "/queue/notifications",
                savedNotification
        );
    }

    public void chatAction(String senderId, String receiverId) {
        Notification notification = new Notification();
        notification.setUserId(new ObjectId(receiverId));
        notification.setType(Type.MESSAGE);
        notification.setContent(senderId + " đã gửi bạn một tin nhắn mới");
        sendNotification(notification);
    }
}