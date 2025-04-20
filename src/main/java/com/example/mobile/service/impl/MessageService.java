package com.example.mobile.service.impl;

import com.example.mobile.dto.ConversationSummaryDTO;
import com.example.mobile.model.Album;
import com.example.mobile.model.Message;
import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.repository.AlbumRepository;
import com.example.mobile.repository.MessageRepository;
import com.example.mobile.repository.ProfileRepository;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.service.IMessageService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService implements IMessageService {
    @Autowired
    private  MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Override
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<ConversationSummaryDTO> getConversationSummaries() {
        // Extract userId from authentication context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("User not authenticated");
        }
        ObjectId currentUserId;
        try {
            currentUserId = new ObjectId(authentication.getName());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user ID in authentication token", e);
        }

        // Find all messages involving the user
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(currentUserId, currentUserId);
        Set<ObjectId> partnerIds = messages.stream()
                .map(m -> m.getSenderId().equals(currentUserId) ? m.getReceiverId() : m.getSenderId())
                .collect(Collectors.toSet());

        List<ConversationSummaryDTO> summaries = new ArrayList<>();
        for (ObjectId partnerId : partnerIds) {
            ConversationSummaryDTO dto = new ConversationSummaryDTO();
            dto.setUserId(partnerId.toString());

            // Fetch user and profile details
            Optional<User> partner = userRepository.findById(partnerId);
            if (partner.isPresent()) {
                Optional<Profile> profile = profileRepository.findById(partner.get().getProfileId());
                profile.ifPresent(p -> dto.setName(p.getFirstName() + " " + p.getLastName()));

                // Fetch profile picture from album
                Optional<Album> album = profile.flatMap(p -> albumRepository.findById(p.getAlbumId()));
                album.ifPresent(a -> dto.setProfilePicture(a.getPic1()));
            }

            // Fetch latest message
            List<Message> conversation = messageRepository
                    .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySendTimeDesc(
                            currentUserId, partnerId, partnerId, currentUserId);
            if (!conversation.isEmpty()) {
                Message latest = conversation.get(0);
                dto.setLatestMessage(latest.getContent());
                dto.setLatestMessageTime(latest.getSendTime());
            }

            // Count unread messages sent by partner to the current user
            long unreadCount = messageRepository.countBySenderIdAndReceiverIdAndIsReadFalse(partnerId, currentUserId);
            dto.setUnreadCount((int) unreadCount);

            summaries.add(dto);
        }

        return summaries;
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
    public void markMessagesAsRead(ObjectId senderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("User not authenticated");
        }
        ObjectId receiverId;
        try {
            receiverId = new ObjectId(authentication.getName());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid user ID in authentication token", e);
        }
        List<Message> unreadMessages = messageRepository.findBySenderIdAndReceiverIdAndIsReadFalse(senderId, receiverId);
        unreadMessages.forEach(message -> message.setRead(true));
        messageRepository.saveAll(unreadMessages);
    }
}
