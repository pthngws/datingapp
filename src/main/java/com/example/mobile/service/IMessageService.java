package com.example.mobile.service;

import com.example.mobile.dto.ConversationSummaryDTO;
import com.example.mobile.model.Message;
import org.bson.types.ObjectId;

import java.util.List;

public interface IMessageService {

    Message sendMessage(Message message);

    List<ConversationSummaryDTO> getConversationSummaries();

    List<Message> getConversation(ObjectId userId1, ObjectId userId2);


    List<Message> getUnreadMessages(ObjectId receiverId);

    void markMessagesAsRead(ObjectId senderId);
}
