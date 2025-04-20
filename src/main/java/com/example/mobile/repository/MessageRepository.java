package com.example.mobile.repository;


import com.example.mobile.model.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(ObjectId senderId1, ObjectId receiverId1, ObjectId senderId2, ObjectId receiverId2);
    List<Message> findByReceiverIdAndIsReadFalse(ObjectId receiverId);


    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderBySendTimeDesc(
            ObjectId senderId1, ObjectId receiverId1, ObjectId senderId2, ObjectId receiverId2);

    List<Message> findBySenderIdOrReceiverId(ObjectId senderId, ObjectId receiverId);

    long countBySenderIdAndReceiverIdAndIsReadFalse(ObjectId senderId, ObjectId receiverId);
    List<Message> findBySenderIdAndReceiverIdAndIsReadFalse(ObjectId senderId, ObjectId receiverId);

}
