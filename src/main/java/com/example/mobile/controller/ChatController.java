package com.example.mobile.controller;

import com.example.mobile.dto.MessageDTO;
import com.example.mobile.model.Message;
import com.example.mobile.model.Notification;
import com.example.mobile.model.enums.Type;
import com.example.mobile.repository.MessageRepository;
import com.example.mobile.repository.NotificationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {
    @Autowired
    private  SimpMessagingTemplate template;
    @Autowired
    private  MessageRepository messageRepository;
    @Autowired
    private NotificationRepository notificationRepository;


    @MessageMapping("/chat")
    public void sendMessage(MessageDTO chatMessage) {
        Message message = new Message();
        message.setSenderId(chatMessage.getSenderId());
        message.setReceiverId(chatMessage.getReceiverId());
        message.setContent(chatMessage.getContent());
        message.setSendTime(LocalDateTime.now());
        messageRepository.save(message);
        template.convertAndSendToUser(String.valueOf(chatMessage.getReceiverId()), "/queue/messages", message);

        // Tạo và gửi thông báo
        Notification noti = new Notification();
        noti.setUserId(new ObjectId(String.valueOf(chatMessage.getReceiverId())));
        noti.setType(Type.MESSAGE);
        noti.setContent("Bạn có tin nhắn mới từ " + chatMessage.getSenderId());
        notificationRepository.save(noti);

        template.convertAndSendToUser(String.valueOf(chatMessage.getReceiverId()), "/queue/notifications", noti);
    }
}
