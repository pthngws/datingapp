package com.example.mobile.controller;

import com.example.mobile.dto.MessageDTO;
import com.example.mobile.model.Message;
import com.example.mobile.repository.MessageRepository;
import com.example.mobile.service.INotificationService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private INotificationService notificationService;

    @MessageMapping("/chat")
    public void sendMessage(MessageDTO chatMessage) {
        System.out.println("Received message from client: " + chatMessage);

        // Tạo và lưu tin nhắn vào database
        Message message = new Message();
        message.setSenderId(new ObjectId(chatMessage.getSenderId()));
        message.setReceiverId(new ObjectId(chatMessage.getReceiverId()));
        message.setContent(chatMessage.getContent());
        message.setSendTime(LocalDateTime.now());
        message.setRead(false);

        Message savedMessage = messageRepository.save(message);

        // Cập nhật MessageDTO để gửi qua WebSocket
        chatMessage.setId(savedMessage.getId().toString());
        chatMessage.setSenderId(savedMessage.getSenderId().toString());
        chatMessage.setReceiverId(savedMessage.getReceiverId().toString());
        chatMessage.setSendTime(savedMessage.getSendTime().format(DateTimeFormatter.ofPattern("HH:mm")));

        // Gửi tin nhắn tới cả người nhận và người gửi
        template.convertAndSendToUser(chatMessage.getReceiverId(), "/queue/messages", chatMessage);
        template.convertAndSendToUser(chatMessage.getSenderId(), "/queue/messages", chatMessage);

        // Gửi thông báo (nếu cần)
        notificationService.chatAction(chatMessage.getSenderId(), chatMessage.getReceiverId());
    }
}