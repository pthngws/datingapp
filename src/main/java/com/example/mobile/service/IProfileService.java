package com.example.mobile.service;

import com.example.mobile.model.enums.Gender;
import com.example.mobile.model.Profile;

import java.util.List;

public interface IProfileService {

    Profile updateProfile(String id, Profile updatedProfile);
    List<Profile> searchProfiles(String firstName, String lastName, Gender gender, Integer age, Integer minAge, Integer maxAge, Integer minHeight, Integer maxHeight);
}
