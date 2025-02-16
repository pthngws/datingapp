package com.example.mobile.service.impl;

import com.example.mobile.model.Profile;
import com.example.mobile.repository.ProfileRepository;
import com.example.mobile.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileService implements IProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public List<Profile> searchProfiles(String firstName, String lastName, Boolean gender, Integer age, Integer minAge, Integer maxAge, Integer minHeight, Integer maxHeight) {
        return profileRepository.searchProfiles(firstName, lastName, gender, age, minAge, maxAge, minHeight, maxHeight);
    }



}
