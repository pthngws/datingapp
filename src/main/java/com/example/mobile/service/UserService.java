package com.example.mobile.service;


import com.example.mobile.model.User;
import com.example.mobile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private HashMap<String, String> otpStorage = new HashMap<>();

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

    @Override
    public void sendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStorage.put(email, otp);

        // Gửi OTP qua email
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public boolean resetPassword(String email, String otp, String newPassword) {
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(newPassword);
                userRepository.save(user);
                otpStorage.remove(email);
                return true;
            }
        }
        return false;
    }

}
