package com.example.mobile.service.impl;


import com.example.mobile.model.User;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getUsersCreatedWithinLast7Days() {
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        return userRepository.findByCreateDateAfter(sevenDaysAgo);
    }

    @Override
    public User findByUsername(String username)
    {
        return userRepository.findByUsername(username) ;
    }

}
