package com.example.mobile.repository;

import com.example.mobile.model.UserImages;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserImagesRepository extends MongoRepository<UserImages, String> {
    Optional<UserImages> findByUserId(String userId);
}