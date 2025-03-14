package com.example.mobile.service.impl;

import com.example.mobile.dto.request.ProfileUpdateDTO;
import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.exception.AppException;
import com.example.mobile.exception.ErrorCode;
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
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
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
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        Optional<Profile> optionalProfile = profileRepository.findById(user.getProfileId());

        if (optionalProfile.isEmpty()) throw new AppException(ErrorCode.PROFILE_NOT_FOUND);

        Profile profile = optionalProfile.get();
        Address address = addressRepository.findById(profile.getAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        Album album = albumRepository.findById(profile.getAlbumId())
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        return ProfileResponse.builder()
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .hobbies(profile.getHobbies()
                        .stream()
                        .map(Hobbies::getDisplayName)
                        .collect(Collectors.toList()))
                .gender(profile.getGender().getDisplayName())
                .age(profile.getAge())
                .height(profile.getHeight())
                .bio(profile.getBio())
                .zodiacSign(profile.getZodiacSign().getDisplayName())
                .personalityType(profile.getPersonalityType())
                .communicationStyle(profile.getCommunicationStyle().getDisplayName())
                .loveLanguage(profile.getLoveLanguage().getDisplayName())
                .petPreference(profile.getPetPreference().getDisplayName())
                .drinkingHabit(profile.getDrinkingHabit().getDisplayName())
                .smokingHabit(profile.getSmokingHabit().getDisplayName())
                .sleepingHabit(profile.getSleepingHabit().getDisplayName())
                .street(address.getStreet())
                .district(address.getDistrict())
                .province(address.getProvince())
                .pic1(album.getPic1())
                .pic2(album.getPic2())
                .pic3(album.getPic3())
                .pic4(album.getPic4())
                .pic5(album.getPic5())
                .pic6(album.getPic6())
                .pic7(album.getPic7())
                .pic8(album.getPic8())
                .pic9(album.getPic9())
                .build();
    }

}
