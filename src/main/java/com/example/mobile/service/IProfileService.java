package com.example.mobile.service;

import com.example.mobile.dto.request.ProfileUpdateDTO;
import com.example.mobile.model.enums.Gender;
import com.example.mobile.model.Profile;
import org.bson.types.ObjectId;

import java.util.List;

public interface IProfileService {

    List<Profile> searchProfiles(String firstName, String lastName, Gender gender, Integer age, Integer minAge, Integer maxAge, Integer minHeight, Integer maxHeight);


    Profile updateProfile(ObjectId id, ProfileUpdateDTO dto);
}
