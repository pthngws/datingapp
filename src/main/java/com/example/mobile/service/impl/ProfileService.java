package com.example.mobile.service.impl;

import com.example.mobile.dto.request.ProfileUpdateDTO;
import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.model.Address;
import com.example.mobile.model.Album;
import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.model.enums.Gender;
import com.example.mobile.model.enums.Hobbies;
import com.example.mobile.repository.AddressRepository;
import com.example.mobile.repository.AlbumRepository;
import com.example.mobile.repository.ProfileRepository;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.service.IProfileService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProfileService implements IProfileService {
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private AddressRepository addressRepository;


    @Override
    public Profile updateProfile(ProfileUpdateDTO updateDTO) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());  // userId lưu trong "sub" của token

        // Lấy profile từ user
        User user = userRepository.findById(userId).get();
        Optional<Profile> optionalProfile = profileRepository.findById(user.getProfileId());

        if (optionalProfile.isEmpty()) {
            throw new RuntimeException("Không tìm thấy Profile với ID: " + user.getProfileId());
        }

        Profile existingProfile = optionalProfile.get();

        if (updateDTO.getFirstName() != null) {
            existingProfile.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            existingProfile.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getHobbies() != null) {
            existingProfile.setHobbies(updateDTO.getHobbies());
        }
        if (updateDTO.getGender() != null) {
            existingProfile.setGender(updateDTO.getGender());
        }
        if (updateDTO.getAge() != null && updateDTO.getAge() > 0) {
            existingProfile.setAge(updateDTO.getAge());
        }
        if (updateDTO.getHeight() != null && updateDTO.getHeight() > 0) {
            existingProfile.setHeight(updateDTO.getHeight());
        }
        if (updateDTO.getBio() != null) {
            existingProfile.setBio(updateDTO.getBio());
        }
        if (updateDTO.getZodiacSign() != null) {
            existingProfile.setZodiacSign(updateDTO.getZodiacSign());
        }
        if (updateDTO.getPersonalityType() != null) {
            existingProfile.setPersonalityType(updateDTO.getPersonalityType());
        }
        if (updateDTO.getCommunicationStyle() != null) {
            existingProfile.setCommunicationStyle(updateDTO.getCommunicationStyle());
        }
        if (updateDTO.getLoveLanguage() != null) {
            existingProfile.setLoveLanguage(updateDTO.getLoveLanguage());
        }
        if (updateDTO.getPetPreference() != null) {
            existingProfile.setPetPreference(updateDTO.getPetPreference());
        }
        if (updateDTO.getDrinkingHabit() != null) {
            existingProfile.setDrinkingHabit(updateDTO.getDrinkingHabit());
        }
        if (updateDTO.getSmokingHabit() != null) {
            existingProfile.setSmokingHabit(updateDTO.getSmokingHabit());
        }
        if (updateDTO.getSleepingHabit() != null) {
            existingProfile.setSleepingHabit(updateDTO.getSleepingHabit());
        }

        return profileRepository.save(existingProfile);
    }


    @Override
    public List<Profile> searchProfiles(String firstName, String lastName, Gender gender, Integer age, Integer minAge, Integer maxAge, Integer minHeight, Integer maxHeight) {
        return profileRepository.searchProfiles(firstName, lastName, gender, age, minAge, maxAge, minHeight, maxHeight);
    }

    @Override
    public ProfileResponse findByUserId(ObjectId userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Profile> optionalProfile = profileRepository.findById(user.getProfileId());

        if (optionalProfile.isEmpty()) {
            throw new RuntimeException("Profile not found");
        }

        Profile profile = optionalProfile.get();
        ProfileResponse profileResponse = new ProfileResponse();
        Address address = addressRepository.findById(profile.getAddressId()).orElseThrow(() -> new RuntimeException("Address not found"));
        // Set dữ liệu từ Profile vào ProfileResponse
        profileResponse.setStreet(address.getStreet());
        profileResponse.setDistrict(address.getDistrict());
        profileResponse.setProvince(address.getProvince());

        Album album = albumRepository.findById(profile.getAlbumId()).orElseThrow(() -> new RuntimeException("Album not found"));
        profileResponse.setPic1(album.getPic1());
        profileResponse.setPic2(album.getPic2());
        profileResponse.setPic3(album.getPic3());
        profileResponse.setPic4(album.getPic4());
        profileResponse.setPic5(album.getPic5());
        profileResponse.setPic6(album.getPic6());
        profileResponse.setPic7(album.getPic7());
        profileResponse.setPic8(album.getPic8());
        profileResponse.setPic9(album.getPic9());

        profileResponse.setFirstName(profile.getFirstName());
        profileResponse.setLastName(profile.getLastName());

        List<String> hobbiesVietnamese = profile.getHobbies()
                .stream()
                .map(Hobbies::getDisplayName) // Gọi phương thức getDisplayName() để lấy tiếng Việt
                .collect(Collectors.toList());
        profileResponse.setHobbies(hobbiesVietnamese);

        profileResponse.setGender(profile.getGender().getDisplayName());
        profileResponse.setAge(profile.getAge());
        profileResponse.setHeight(profile.getHeight());
        profileResponse.setBio(profile.getBio());
        profileResponse.setZodiacSign(profile.getZodiacSign().getDisplayName());
        profileResponse.setPersonalityType(profile.getPersonalityType());
        profileResponse.setCommunicationStyle(profile.getCommunicationStyle().getDisplayName());
        profileResponse.setLoveLanguage(profile.getLoveLanguage().getDisplayName());
        profileResponse.setPetPreference(profile.getPetPreference().getDisplayName());
        profileResponse.setDrinkingHabit(profile.getDrinkingHabit().getDisplayName());
        profileResponse.setSmokingHabit(profile.getSmokingHabit().getDisplayName());
        profileResponse.setSleepingHabit(profile.getSleepingHabit().getDisplayName());

        return profileResponse;
    }

}
