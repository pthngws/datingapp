package com.example.mobile.service.impl;

import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.model.Relationship;
import com.example.mobile.model.enums.RelationshipStatus;
import com.example.mobile.repository.RelationshipRepository;
import com.example.mobile.service.IProfileService;
import com.example.mobile.service.IRelationshipService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationshipService implements IRelationshipService {

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private IProfileService profileService;

    @Transactional
    @Override
    public String likeUser(String targetUserId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());
        ObjectId userId2 = new ObjectId(targetUserId);

        Optional<Relationship> existingRelation = relationshipRepository.findByUserId1AndUserId2(userId, userId2);
        Relationship relationship = existingRelation.orElseGet(() -> {
            Relationship newRel = new Relationship();
            newRel.setUserId1(userId);
            newRel.setUserId2(userId2);
            newRel.setStatus(RelationshipStatus.LIKED);
            return relationshipRepository.save(newRel);
        });

        Optional<Relationship> reverseRelation = relationshipRepository.findByUserId1AndUserId2(userId2, userId);
        if (reverseRelation.isPresent() && reverseRelation.get().getStatus() == RelationshipStatus.LIKED) {
            relationship.setStatus(RelationshipStatus.MATCHED);
            Relationship reverse = reverseRelation.get();
            reverse.setStatus(RelationshipStatus.MATCHED);
            relationshipRepository.save(relationship);
            relationshipRepository.save(reverse);
            return "It's a match!";
        }

        return "You liked " + targetUserId;
    }

    @Transactional
    @Override
    public String skipUser(String targetUserId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());
        ObjectId userId2 = new ObjectId(targetUserId);

        Optional<Relationship> existingRelation = relationshipRepository.findByUserId1AndUserId2(userId, userId2);
        if (existingRelation.isPresent() && existingRelation.get().getStatus() == RelationshipStatus.SKIPPED) {
            return "You already skipped this user.";
        }

        Relationship relationship = existingRelation.orElseGet(() -> {
            Relationship newRel = new Relationship();
            newRel.setUserId1(userId);
            newRel.setUserId2(userId2);
            return newRel;
        });

        relationship.setStatus(RelationshipStatus.SKIPPED);
        relationshipRepository.save(relationship);
        return "You skipped " + targetUserId;
    }

    @Transactional
    @Override
    public String blockUser(String targetUserId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());
        ObjectId userId2 = new ObjectId(targetUserId);

        Optional<Relationship> existingRelation = relationshipRepository.findByUserId1AndUserId2(userId, userId2);
        Relationship relationship = existingRelation.orElseGet(() -> {
            Relationship newRel = new Relationship();
            newRel.setUserId1(userId);
            newRel.setUserId2(userId2);
            return newRel;
        });

        relationship.setStatus(RelationshipStatus.BLOCKED);
        relationshipRepository.save(relationship);

        return "You have blocked " + targetUserId;
    }

    @Transactional
    @Override
    public String unmatchUser(String targetUserId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());
        ObjectId userId2 = new ObjectId(targetUserId);

        Optional<Relationship> existingRelation = relationshipRepository.findByUserId1AndUserId2(userId, userId2);
        Optional<Relationship> reverseRelation = relationshipRepository.findByUserId1AndUserId2(userId2, userId);

        if (existingRelation.isPresent() && existingRelation.get().getStatus() == RelationshipStatus.MATCHED) {
            existingRelation.get().setStatus(RelationshipStatus.LIKED);
            relationshipRepository.save(existingRelation.get());
        }

        if (reverseRelation.isPresent() && reverseRelation.get().getStatus() == RelationshipStatus.MATCHED) {
            reverseRelation.get().setStatus(RelationshipStatus.LIKED);
            relationshipRepository.save(reverseRelation.get());
        }

        return "You have unmatched with " + targetUserId;
    }

    @Transactional
    @Override
    public String unblockUser(String targetUserId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());
        ObjectId userId2 = new ObjectId(targetUserId);

        Optional<Relationship> existingRelation = relationshipRepository.findByUserId1AndUserId2(userId, userId2);
        if (existingRelation.isPresent() && existingRelation.get().getStatus() == RelationshipStatus.BLOCKED) {
            relationshipRepository.delete(existingRelation.get());
            return "You have unblocked " + targetUserId;
        }

        return "This user is not blocked.";
    }

    @Override
    public List<ProfileResponse> getUsersWhoLikedMe() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());

        List<Relationship> likedByOthers = relationshipRepository.findByUserId2AndStatus(userId, RelationshipStatus.LIKED);

        return likedByOthers.stream()
                .map(rel -> profileService.findByUserId(rel.getUserId1()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProfileResponse> getUsersWhoMatchedMe() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());

        List<Relationship> matchedUsers = relationshipRepository.findByUserId2AndStatus(userId, RelationshipStatus.MATCHED);

        return matchedUsers.stream()
                .map(rel -> profileService.findByUserId(rel.getUserId1()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isBlockedByUser(String targetUserId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());
        ObjectId targetId = new ObjectId(targetUserId);

        // Kiểm tra nếu targetUser đã block mình
        Optional<Relationship> reverseRelation = relationshipRepository.findByUserId1AndUserId2(targetId, userId);
        return reverseRelation.isPresent() && reverseRelation.get().getStatus() == RelationshipStatus.BLOCKED;
    }

}
