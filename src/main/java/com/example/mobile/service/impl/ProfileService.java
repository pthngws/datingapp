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
    public List<Profile> getProfilesByFirstName(String firstName) {
        return profileRepository.findByFirstNameContainingIgnoreCase(firstName);
    }
    @Override
    public List<Profile> getProfilesByLastName(String lastName) {
        return profileRepository.findByLastNameContainingIgnoreCase(lastName);
    }
    @Override
    public List<Profile> getProfilesByGender(boolean gender) {
        return profileRepository.findByGender(gender);
    }
    @Override
    public List<Profile> getProfilesByAge(int age) {
        return profileRepository.findByAge(age);
    }
    @Override
    public List<Profile> getProfilesByAgeRange(int minAge, int maxAge) {
        return profileRepository.findByAgeBetween(minAge, maxAge);
    }
    @Override
    public List<Profile> getProfilesByHeightRange(int minHeight, int maxHeight) {
        return profileRepository.findByHeightBetween(minHeight, maxHeight);
    }


}
