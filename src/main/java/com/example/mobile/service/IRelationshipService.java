package com.example.mobile.service;

import com.example.mobile.dto.response.UserInfoResponse;
import org.bson.types.ObjectId;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IRelationshipService {
    @Transactional
    String likeUser(String targetUserId);

    @Transactional
    String skipUser(String targetUserId);

    @Transactional
    String blockUser(String targetUserId);

    @Transactional
    String unmatchUser(String targetUserId);

    @Transactional
    String unblockUser(String targetUserId);

    List<UserInfoResponse> getUsersWhoLikedMe();

    List<UserInfoResponse> getUsersWhoMatchedMe();

    boolean isBlockedByUser(String targetUserId);


    List<ObjectId> filterUserIdsWithRelationship(List<ObjectId> userIds);
}
