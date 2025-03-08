package com.example.mobile.service.impl;

import com.example.mobile.model.Gender;
import com.example.mobile.model.Profile;
import com.example.mobile.repository.ProfileRepository;
import com.example.mobile.service.IProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService implements IProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Profile updateProfile(String id, Profile updatedProfile) {
        Optional<Profile> optionalProfile = profileRepository.findById(id);

        if (optionalProfile.isPresent()) {
            Profile existingProfile = optionalProfile.get();

            if (updatedProfile.getFirstName() != null) {
                existingProfile.setFirstName(updatedProfile.getFirstName());
            }
            if (updatedProfile.getLastName() != null) {
                existingProfile.setLastName(updatedProfile.getLastName());
            }

            existingProfile.setGender(updatedProfile.getGender());

            // age và height là int, kiểm tra giá trị hợp lệ (> 0)
            if (updatedProfile.getAge() > 0) {
                existingProfile.setAge(updatedProfile.getAge());
            }
            if (updatedProfile.getHeight() > 0) {
                existingProfile.setHeight(updatedProfile.getHeight());
            }

            if (updatedProfile.getAddress() != null) {
                existingProfile.setAddress(updatedProfile.getAddress());
            }

            return profileRepository.save(existingProfile);
        } else {
            throw new RuntimeException("Không tìm thấy Profile với ID: " + id);
        }
    }

    @Override
    public List<Profile> searchProfiles(String firstName, String lastName, Gender gender, Integer age, Integer minAge, Integer maxAge, Integer minHeight, Integer maxHeight) {
        return profileRepository.searchProfiles(firstName, lastName, gender, age, minAge, maxAge, minHeight, maxHeight);
    }



}
