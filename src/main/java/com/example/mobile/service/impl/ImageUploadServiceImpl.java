package com.example.mobile.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.mobile.exception.AppException;
import com.example.mobile.exception.ErrorCode;
import com.example.mobile.model.Album;
import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.repository.AlbumRepository;
import com.example.mobile.repository.ProfileRepository;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.service.IImageUploadService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageUploadServiceImpl implements IImageUploadService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public String uploadSingleImage(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }

    @Override
    public Album uploadImage(MultipartFile file, String picPosition) throws Exception {
        // Get userId from token
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        ObjectId userId = new ObjectId(authentication.getName());

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Find profile
        Profile profile = profileRepository.findById(user.getProfileId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Get or create album
        Album userImages = albumRepository.findById(profile.getAlbumId())
                .orElseGet(() -> new Album());

        // Upload single image
        String imageUrl = uploadSingleImage(file);

        // Set image to specified position
        switch (picPosition.toLowerCase()) {
            case "pic1":
                userImages.setPic1(imageUrl);
                break;
            case "pic2":
                userImages.setPic2(imageUrl);
                break;
            case "pic3":
                userImages.setPic3(imageUrl);
                break;
            case "pic4":
                userImages.setPic4(imageUrl);
                break;
            case "pic5":
                userImages.setPic5(imageUrl);
                break;
            case "pic6":
                userImages.setPic6(imageUrl);
                break;
            case "pic7":
                userImages.setPic7(imageUrl);
                break;
            case "pic8":
                userImages.setPic8(imageUrl);
                break;
            case "pic9":
                userImages.setPic9(imageUrl);
                break;
            default:
                throw new Exception("Invalid pic position. Must be pic1 to pic9");
        }

        // Save and return updated album
        return albumRepository.save(userImages);
    }
}