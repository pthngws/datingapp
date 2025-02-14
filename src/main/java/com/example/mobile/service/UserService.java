package com.example.mobile.service;


import com.example.mobile.model.User;
import com.example.mobile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    @Override
    public User createUser(User user) {
        try {
            user.setCreateDate(LocalDate.now());
            return userRepository.save(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
    }

    @Override
    public List<User> getUsersCreatedWithinLast7Days() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        return userRepository.findByCreateDateAfter(sevenDaysAgo);
    }


    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    @Override
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

}
