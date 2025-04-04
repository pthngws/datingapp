package com.example.mobile.service.impl;

import com.example.mobile.model.Message;
import com.example.mobile.repository.MessageRepository;
import com.example.mobile.service.IMessageService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService implements IMessageService {
    @Autowired
    private  MessageRepository messageRepository;

    @Override
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }
    @Override
    public List<Message> getConversation(ObjectId userId1, ObjectId userId2) {
        return messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(userId1, userId2, userId1, userId2);
    }

    @Override
    public List<Message> getUnreadMessages(ObjectId receiverId) {
        return messageRepository.findByReceiverIdAndIsReadFalse(receiverId);
    }

    @Override
    public void markMessagesAsRead(ObjectId receiverId) {
        List<Message> unreadMessages = messageRepository.findByReceiverIdAndIsReadFalse(receiverId);
        unreadMessages.forEach(message -> message.setRead(true));
        messageRepository.saveAll(unreadMessages);
    }
}
