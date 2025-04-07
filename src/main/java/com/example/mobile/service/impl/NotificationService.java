package com.example.mobile.service.impl;

import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.model.Notification;
import com.example.mobile.model.enums.Type;
import com.example.mobile.repository.NotificationRepository;
import com.example.mobile.service.INotificationService;
import com.example.mobile.service.IProfileService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements INotificationService {

    @Autowired
    private IProfileService profileService;

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public void chatAction(String senderId, String receiverId) {
        Notification notification = new Notification();
        notification.setUserId(new ObjectId(receiverId));
        notification.setType(Type.MESSAGE);
        ObjectId objectId = new ObjectId(senderId);
        ProfileResponse profile = profileService.findByUserId(objectId);
        notification.setContent(profile.getFirstName() + " " + profile.getLastName() + " đã gửi cho bạn 1 tin nhắn.");
        sendNotification(notification);
    }

    @Override
    public void likeAction(String senderId, String receiverId) {
        Notification notification = new Notification();
        notification.setUserId(new ObjectId(receiverId));
        notification.setType(Type.LIKE);
        ObjectId objectId = new ObjectId(senderId);
        ProfileResponse profile = profileService.findByUserId(objectId);
        notification.setContent("Bạn nhận 1 lượt thích mới.");
        sendNotification(notification);
    }

    @Override
    public void matchAction(String senderId, String receiverId) {
        Notification notification = new Notification();
        notification.setUserId(new ObjectId(receiverId));
        notification.setType(Type.MATCH);
        ObjectId objectId = new ObjectId(senderId);
        ProfileResponse profile = profileService.findByUserId(objectId);
        notification.setContent("Bạn và " + profile.getFirstName() + " " + profile.getLastName() + " đã kết nối với nhau.");
        sendNotification(notification);
    }

    public void sendNotification(Notification notification) {
        Notification savedNotification = this.saveNotification(notification);
        System.out.println("Sending notification to user: " + savedNotification.getUserId());

        messagingTemplate.convertAndSendToUser(
                savedNotification.getUserId().toHexString(),
                "/queue/notifications",
                savedNotification
        );
    }
}
