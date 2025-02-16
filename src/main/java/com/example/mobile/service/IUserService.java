package com.example.mobile.service;

import com.example.mobile.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> getAllUsers();

    Optional<User> getUserById(String id);

    User createUser(User user);

    List<User> getUsersCreatedWithinLast7Days();

    void deleteUser(String id);

    Optional<User> login(String username, String password);

    void sendOtp(String email);

    boolean resetPassword(String email, String otp, String newPassword);
}
