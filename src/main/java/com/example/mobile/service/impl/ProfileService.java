package com.example.mobile.service.impl;

import com.example.mobile.dto.request.ProfileUpdateDTO;
import com.example.mobile.model.Profile;
import com.example.mobile.model.enums.Gender;
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
    public Profile updateProfile(String id, ProfileUpdateDTO dto) {
        Optional<Profile> optionalProfile = profileRepository.findById(id);

        if (optionalProfile.isEmpty()) {
            throw new RuntimeException("Không tìm thấy Profile với ID: " + id);
        }

        Profile existingProfile = optionalProfile.get();

        if (dto.getFirstName() != null) {
            existingProfile.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            existingProfile.setLastName(dto.getLastName());
        }
        if (dto.getHobbies() != null) {
            existingProfile.setHobbies(dto.getHobbies());
        }
        if (dto.getGender() != null) {
            existingProfile.setGender(dto.getGender());
        }
        if (dto.getAge() != null && dto.getAge() > 0) {
            existingProfile.setAge(dto.getAge());
        }
        if (dto.getHeight() != null && dto.getHeight() > 0) {
            existingProfile.setHeight(dto.getHeight());
        }
        if (dto.getAddress() != null) {
            existingProfile.setAddress(dto.getAddress());
        }
        if (dto.getBio() != null) {
            existingProfile.setBio(dto.getBio());
        }
        if (dto.getZodiacSign() != null) {
            existingProfile.setZodiacSign(dto.getZodiacSign());
        }
        if (dto.getPersonalityType() != null) {
            existingProfile.setPersonalityType(dto.getPersonalityType());
        }
        if (dto.getCommunicationStyle() != null) {
            existingProfile.setCommunicationStyle(dto.getCommunicationStyle());
        }
        if (dto.getLoveLanguage() != null) {
            existingProfile.setLoveLanguage(dto.getLoveLanguage());
        }
        if (dto.getPetPreference() != null) {
            existingProfile.setPetPreference(dto.getPetPreference());
        }
        if (dto.getDrinkingHabit() != null) {
            existingProfile.setDrinkingHabit(dto.getDrinkingHabit());
        }
        if (dto.getSmokingHabit() != null) {
            existingProfile.setSmokingHabit(dto.getSmokingHabit());
        }
        if (dto.getSleepingHabit() != null) {
            existingProfile.setSleepingHabit(dto.getSleepingHabit());
        }

        return profileRepository.save(existingProfile);
    }


    @Override
    public List<Profile> searchProfiles(String firstName, String lastName, Gender gender, Integer age, Integer minAge, Integer maxAge, Integer minHeight, Integer maxHeight) {
        return profileRepository.searchProfiles(firstName, lastName, gender, age, minAge, maxAge, minHeight, maxHeight);
    }



}
