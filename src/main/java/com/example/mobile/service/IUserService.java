package com.example.mobile.service;

import com.example.mobile.model.User;
import org.bson.types.ObjectId;

import java.util.List;

public interface IUserService {
    List<User> getUsersCreatedWithinLast7Days();

    User findByUsername(String username);

    User findUserById(ObjectId id);
}
