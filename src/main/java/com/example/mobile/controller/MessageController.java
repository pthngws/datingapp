package com.example.mobile.controller;

import com.example.mobile.dto.MessageDTO;
import com.example.mobile.model.Message;
import com.example.mobile.service.IMessageService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @GetMapping("/conversation")
    public List<MessageDTO> getConversation(@RequestParam String userId1, @RequestParam String userId2) {
        List<Message> messages = messageService.getConversation(new ObjectId(userId1), new ObjectId(userId2));
        return messages.stream().map(m -> {
            MessageDTO dto = new MessageDTO();
            dto.setId(m.getId().toString());
            dto.setSenderId(m.getSenderId().toString());
            dto.setReceiverId(m.getReceiverId().toString());
            dto.setContent(m.getContent());
            dto.setSendTime(m.getSendTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            dto.setRead(m.isRead());
            return dto;
        }).collect(Collectors.toList());
    }
}