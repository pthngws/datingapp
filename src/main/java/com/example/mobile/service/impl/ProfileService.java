package com.example.mobile.service.impl;

import com.example.mobile.dto.request.LocationUpdateDto;
import com.example.mobile.dto.request.ProfileUpdateDTO;
import com.example.mobile.dto.response.ProfileResponse;
import com.example.mobile.exception.AppException;
import com.example.mobile.exception.ErrorCode;
import com.example.mobile.model.Address;
import com.example.mobile.model.Album;
import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.model.enums.*;
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
    public List<Profile> searchProfiles(Gender gender, Integer minAge, Integer maxAge,  Double maxDistance) {
        // Lấy danh sách hồ sơ theo các tiêu chí hiện có
        List<Profile> profiles = profileRepository.searchProfiles(gender, minAge, maxAge);

        // Lấy thông tin người dùng hiện tại
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId currentUserId = new ObjectId(authentication.getName());
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        Optional<Profile> currentProfileOpt = profileRepository.findById(currentUser.getProfileId());
        if (currentProfileOpt.isEmpty()) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
        Profile currentProfile = currentProfileOpt.get();

        // Nếu không có maxDistance, chỉ loại trừ người dùng hiện tại và trả về danh sách
        if (maxDistance == null) {
            return profiles.stream()
                    .filter(profile -> !profile.getId().equals(currentProfile.getId())) // Loại trừ người dùng hiện tại
                    .collect(Collectors.toList());
        }

        // Lấy vị trí của người dùng hiện tại
        Address currentAddress = addressRepository.findById(currentProfile.getAddressId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        // Lọc hồ sơ theo khoảng cách và loại trừ người dùng hiện tại
        return profiles.stream()
                .filter(profile -> {
                    // Loại trừ người dùng hiện tại
                    if (profile.getId().equals(currentProfile.getId())) {
                        return false;
                    }

                    try {
                        Address address = addressRepository.findById(profile.getAddressId())
                                .orElse(null);
                        if (address == null) {
                            return false; // Bỏ qua nếu không có địa chỉ
                        }
                        double distance = calculateDistance(
                                currentAddress.getLatitude(), currentAddress.getLongitude(),
                                address.getLatitude(), address.getLongitude()
                        );
                        return distance <= maxDistance; // Chỉ giữ lại hồ sơ trong bán kính maxDistance
                    } catch (Exception e) {
                        return false; // Bỏ qua nếu có lỗi khi tính khoảng cách
                    }
                })
                .collect(Collectors.toList());
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
                .firstName(Optional.ofNullable(profile).map(Profile::getFirstName).orElse(null))
                .lastName(Optional.ofNullable(profile).map(Profile::getLastName).orElse(null))
                .hobbies(Optional.ofNullable(profile)
                        .map(Profile::getHobbies)
                        .map(hobbies -> hobbies.stream()
                                .map(Hobbies::getDisplayName)
                                .collect(Collectors.toList()))
                        .orElse(null))
                .gender(Optional.ofNullable(profile)
                        .map(Profile::getGender)
                        .map(Gender::getDisplayName)
                        .orElse(null))
                .age(Optional.ofNullable(profile).map(Profile::getAge).orElse(null))
                .height(Optional.ofNullable(profile).map(Profile::getHeight).orElse(null))
                .bio(Optional.ofNullable(profile).map(Profile::getBio).orElse(null))
                .zodiacSign(Optional.ofNullable(profile)
                        .map(Profile::getZodiacSign)
                        .map(ZodiacSign::getDisplayName)
                        .orElse(null))
                .personalityType(Optional.ofNullable(profile).map(Profile::getPersonalityType).orElse(null))
                .communicationStyle(Optional.ofNullable(profile)
                        .map(Profile::getCommunicationStyle)
                        .map(CommunicationStyle::getDisplayName)
                        .orElse(null))
                .loveLanguage(Optional.ofNullable(profile)
                        .map(Profile::getLoveLanguage)
                        .map(LoveLanguage::getDisplayName)
                        .orElse(null))
                .petPreference(Optional.ofNullable(profile)
                        .map(Profile::getPetPreference)
                        .map(PetPreference::getDisplayName)
                        .orElse(null))
                .drinkingHabit(Optional.ofNullable(profile)
                        .map(Profile::getDrinkingHabit)
                        .map(DrinkingHabit::getDisplayName)
                        .orElse(null))
                .smokingHabit(Optional.ofNullable(profile)
                        .map(Profile::getSmokingHabit)
                        .map(SmokingHabit::getDisplayName)
                        .orElse(null))
                .sleepingHabit(Optional.ofNullable(profile)
                        .map(Profile::getSleepingHabit)
                        .map(SleepingHabit::getDisplayName)
                        .orElse(null))
                .street(Optional.ofNullable(address).map(Address::getStreet).orElse(null))
                .district(Optional.ofNullable(address).map(Address::getDistrict).orElse(null))
                .province(Optional.ofNullable(address).map(Address::getProvince).orElse(null))
                .pic1(Optional.ofNullable(album).map(Album::getPic1).orElse(null))
                .pic2(Optional.ofNullable(album).map(Album::getPic2).orElse(null))
                .pic3(Optional.ofNullable(album).map(Album::getPic3).orElse(null))
                .pic4(Optional.ofNullable(album).map(Album::getPic4).orElse(null))
                .pic5(Optional.ofNullable(album).map(Album::getPic5).orElse(null))
                .pic6(Optional.ofNullable(album).map(Album::getPic6).orElse(null))
                .pic7(Optional.ofNullable(album).map(Album::getPic7).orElse(null))
                .pic8(Optional.ofNullable(album).map(Album::getPic8).orElse(null))
                .pic9(Optional.ofNullable(album).map(Album::getPic9).orElse(null))
                .build();
    }

    @Override
    public void updateLocation(LocationUpdateDto request) {
        // Lấy thông tin người dùng hiện tại từ token
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());

        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Tìm profile
        Optional<Profile> optionalProfile = profileRepository.findById(user.getProfileId());
        if (optionalProfile.isEmpty()) {
            throw new AppException(ErrorCode.PROFILE_NOT_FOUND);
        }
        Profile profile = optionalProfile.get();

        // Tìm address
        Optional<Address> optionalAddress = addressRepository.findById(profile.getAddressId());
        if (optionalAddress.isEmpty()) {
            throw new AppException(ErrorCode.ADDRESS_NOT_FOUND);
        }
        Address address = optionalAddress.get();

        // Cập nhật latitude và longitude
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        // Lưu address
        addressRepository.save(address);
    }

    // Phương thức tính khoảng cách giữa hai điểm (Haversine formula)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Bán kính Trái Đất (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Khoảng cách tính bằng km
    }
}
