package com.example.mobile.service.impl;

import com.example.mobile.dto.response.UserInfoResponse;
import com.example.mobile.exception.AppException;
import com.example.mobile.exception.ErrorCode;
import com.example.mobile.model.Profile;
import com.example.mobile.model.Relationship;
import com.example.mobile.model.User;
import com.example.mobile.model.enums.RelationshipStatus;
import com.example.mobile.repository.AlbumRepository;
import com.example.mobile.repository.ProfileRepository;
import com.example.mobile.repository.RelationshipRepository;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.service.IRelationshipService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationshipService implements IRelationshipService {

    private static final Logger logger = LoggerFactory.getLogger(RelationshipService.class);

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    @Override
    public String likeUser(String targetUserId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

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
                notificationService.matchAction(userId.toHexString(),userId2.toHexString());
                notificationService.matchAction(userId2.toHexString(),userId.toHexString());
                return "Đã ghép đôi thành công!";
            }
            notificationService.likeAction(userId.toHexString(),userId2.toHexString());
            return "Bạn đã thích " + targetUserId;
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Transactional
    @Override
    public String skipUser(String targetUserId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            ObjectId userId = new ObjectId(authentication.getName());
            ObjectId userId2 = new ObjectId(targetUserId);

            Optional<Relationship> existingRelation = relationshipRepository.findByUserId1AndUserId2(userId, userId2);
            if (existingRelation.isPresent() && existingRelation.get().getStatus() == RelationshipStatus.SKIPPED) {
                return "Bạn đã bỏ qua người dùng này rồi.";
            }

            Relationship relationship = existingRelation.orElseGet(() -> {
                Relationship newRel = new Relationship();
                newRel.setUserId1(userId);
                newRel.setUserId2(userId2);
                return newRel;
            });

            relationship.setStatus(RelationshipStatus.SKIPPED);
            relationshipRepository.save(relationship);
            return "Bạn đã bỏ qua " + targetUserId;
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Transactional
    @Override
    public String blockUser(String targetUserId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
               throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

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

            // Xóa quan hệ ngược lại (nếu có)
            Optional<Relationship> reverseRelation = relationshipRepository.findByUserId1AndUserId2(userId2, userId);
            reverseRelation.ifPresent(relationshipRepository::delete);

            return "Bạn đã chặn " + targetUserId;
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        } catch (DataAccessException e) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Transactional
    @Override
    public String unmatchUser(String targetUserId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

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

            return "Bạn đã hủy ghép đôi với " + targetUserId;
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Transactional
    @Override
    public String unblockUser(String targetUserId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            ObjectId userId = new ObjectId(authentication.getName());
            ObjectId userId2 = new ObjectId(targetUserId);

            Optional<Relationship> existingRelation = relationshipRepository.findByUserId1AndUserId2(userId, userId2);
            if (existingRelation.isPresent() && existingRelation.get().getStatus() == RelationshipStatus.BLOCKED) {
                relationshipRepository.delete(existingRelation.get());
                return "Bạn đã bỏ chặn " + targetUserId;
            }

            return "Người dùng này không bị chặn.";
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Override
    public List<UserInfoResponse> getUsersWhoLikedMe() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            ObjectId userId = new ObjectId(authentication.getName());
            List<Relationship> likedByOthers = relationshipRepository.findByUserId2AndStatus(userId, RelationshipStatus.LIKED);

            return likedByOthers.stream()
                    .map(rel -> {
                        try {
                            // Lấy User từ userId (rel.getUserId1())
                            User user = userRepository.findById(rel.getUserId1())
                                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

                            // Lấy Profile từ profileId trong User
                            Profile profile = profileRepository.findById(user.getProfileId())
                                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

                            return convertToUserInfoResponse(profile, user.getId().toString());
                        } catch (AppException e) {
                            throw e;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Override
    public List<UserInfoResponse> getUsersWhoMatchedMe() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            ObjectId userId = new ObjectId(authentication.getName());
            List<Relationship> likedByOthers = relationshipRepository.findByUserId2AndStatus(userId, RelationshipStatus.MATCHED);

            return likedByOthers.stream()
                    .map(rel -> {
                        try {
                            // Lấy User từ userId (rel.getUserId1())
                            User user = userRepository.findById(rel.getUserId1())
                                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

                            // Lấy Profile từ profileId trong User
                            Profile profile = profileRepository.findById(user.getProfileId())
                                    .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));

                            return convertToUserInfoResponse(profile, user.getId().toString());
                        } catch (AppException e) {
                            throw e;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    private UserInfoResponse convertToUserInfoResponse(Profile profile, String userId) {
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(userId);               // Lấy từ User
        response.setAge(profile.getAge());        // Lấy từ Profile
        response.setFirstName(profile.getFirstName()); // Lấy từ Profile
        response.setLastName(profile.getLastName());   // Lấy từ Profile
        response.setPic1(albumRepository.findById(profile.getAlbumId()).get().getPic1()); // Giả sử pic1 là từ albumId
        return response;
    }

    @Override
    public boolean isBlockedByUser(String targetUserId) {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            ObjectId userId = new ObjectId(authentication.getName());
            ObjectId targetId = new ObjectId(targetUserId);

            Optional<Relationship> reverseRelation = relationshipRepository.findByUserId1AndUserId2(targetId, userId);
            return reverseRelation.isPresent() && reverseRelation.get().getStatus() == RelationshipStatus.BLOCKED;
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        } catch (Exception e) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Transactional
    @Override
    public List<ObjectId> filterUserIdsWithRelationship(List<ObjectId> userIds) {
        try {
            // Lấy userId của người dùng hiện tại từ security context
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getName() == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            ObjectId currentUserId = new ObjectId(authentication.getName());

            // Tìm tất cả các relationship mà user1 là currentUserId
            List<Relationship> relationships = relationshipRepository.findByUserId1(currentUserId);

            // Lọc ra userId2 mà userId1 là currentUserId và userId2 nằm trong danh sách userIds
            List<ObjectId> filteredUserIds = relationships.stream()
                    .map(r -> r.getUserId2()) // Lấy userId2
                    .filter(userIds::contains) // Chỉ lấy các userId2 có trong userIds
                    .collect(Collectors.toList());

            return filteredUserIds;
        } catch (Exception e) {
            throw e;
        }
    }


}
