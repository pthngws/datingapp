package com.example.mobile.controller;

import com.example.mobile.model.Message;
import com.example.mobile.service.IMessageService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private IMessageService messageService;

    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        return messageService.sendMessage(message);
    }

    @GetMapping("/conversation")
    public List<Message> getConversation(@RequestParam ObjectId userId1, @RequestParam ObjectId userId2) {
        return messageService.getConversation(userId1, userId2);
    }

    @GetMapping("/unread")
    public List<Message> getUnreadMessages(@RequestParam ObjectId receiverId) {
        return messageService.getUnreadMessages(receiverId);
    }

    @PostMapping("/mark-as-read")
    public void markAsRead(@RequestParam ObjectId receiverId) {
        messageService.markMessagesAsRead(receiverId);
    }

}
