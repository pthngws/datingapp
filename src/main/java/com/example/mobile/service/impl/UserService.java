package com.example.mobile.service.impl;


import com.example.mobile.model.User;
import com.example.mobile.repository.RelationshipRepository;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.service.IUserService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public List<User> getUsersCreatedWithinLast7Days() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        return userRepository.findByCreateAtAfter(sevenDaysAgo);
    }

    @Override
    public List<User> getUsersCreatedWithinLast7DaysExcludingRelationships() {
        // Lấy userId của người dùng hiện tại từ token
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("User not authenticated");
        }
        ObjectId currentUserId = new ObjectId(authentication.getName());

        // Lấy danh sách người dùng mới trong 7 ngày
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<User> recentUsers = userRepository.findByCreateAtAfter(sevenDaysAgo);

        // Lấy danh sách userId mà người dùng hiện tại đã có quan hệ
        List<ObjectId> relatedUserIds = relationshipRepository.findByUserId1(currentUserId)
                .stream()
                .map(relationship -> relationship.getUserId2())
                .collect(Collectors.toList());

        // Loại bỏ chính người dùng hiện tại và những người đã có quan hệ
        return recentUsers.stream()
                .filter(user -> !user.getId().equals(currentUserId)) // Loại bỏ chính người dùng hiện tại
                .filter(user -> !relatedUserIds.contains(user.getId())) // Loại bỏ những người đã có quan hệ
                .collect(Collectors.toList());
    }

    @Override
    public User findByUsername(String username)
    {
        return userRepository.findByUsername(username) ;
    }

    @Override
    public User findById(ObjectId id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

}
