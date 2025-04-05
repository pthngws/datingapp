package com.example.mobile.controller;

import com.example.mobile.dto.MessageDTO;
import com.example.mobile.model.Message;
import com.example.mobile.repository.MessageRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private NotificationController notificationController;

    @MessageMapping("/chat")
    public void sendMessage(MessageDTO chatMessage) {
        System.out.println("Received message from client: " + chatMessage);

        Message message = new Message();
        message.setSenderId(new ObjectId(String.valueOf(chatMessage.getSenderId())));
        message.setReceiverId(new ObjectId(String.valueOf(chatMessage.getReceiverId())));
        message.setContent(chatMessage.getContent());
        message.setSendTime(LocalDateTime.now());
        messageRepository.save(message);

        // Convert ObjectId to String trước khi gửi qua WebSocket
        chatMessage.setSenderId(message.getSenderId());
        chatMessage.setReceiverId(message.getReceiverId());

        template.convertAndSendToUser(String.valueOf(chatMessage.getReceiverId()), "/queue/messages", chatMessage);
        template.convertAndSendToUser(String.valueOf(chatMessage.getSenderId()), "/queue/messages", chatMessage);

        System.out.println("Triggering notification for receiver: " + chatMessage.getReceiverId());
        notificationController.chatAction(String.valueOf(chatMessage.getSenderId()), String.valueOf(chatMessage.getReceiverId()));
    }

}