package com.example.mobile.service;

import com.example.mobile.dto.request.LocationUpdateDto;
import com.example.mobile.dto.request.ProfileUpdateDTO;
import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.model.Profile;
import com.example.mobile.model.enums.Gender;
import org.bson.types.ObjectId;

import java.util.List;

public interface IProfileService {

    List<Profile> searchProfiles(Gender gender, Integer minAge, Integer maxAge, Double maxDistance);
    Profile updateProfile(ProfileUpdateDTO profileUpdateDTO);

    ProfileResponse findByUserId(ObjectId userId);

    void updateLocation(LocationUpdateDto request);
}
