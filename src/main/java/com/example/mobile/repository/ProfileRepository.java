package com.example.mobile.repository;


import com.example.mobile.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProfileRepository extends MongoRepository<Profile, String> {
    List<Profile> findByFirstNameContainingIgnoreCase(String firstName);

    List<Profile> findByLastNameContainingIgnoreCase(String lastName);

    List<Profile> findByGender(boolean gender);

    List<Profile> findByAge(int age);

    List<Profile> findByAgeBetween(int minAge, int maxAge);

    List<Profile> findByHeightBetween(int minHeight, int maxHeight);

}
