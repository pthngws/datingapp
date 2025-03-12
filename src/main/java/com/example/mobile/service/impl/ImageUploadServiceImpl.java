package com.example.mobile.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.mobile.model.UserImages;
import com.example.mobile.repository.UserImagesRepository;
import com.example.mobile.service.IImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    private UserImagesRepository userImagesRepository;

    private static final int MAX_IMAGES = 9;

    /**
     * Upload một ảnh duy nhất lên Cloudinary
     */
    @Override
    public String uploadSingleImage(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }

    /**
     * Upload nhiều ảnh cho user từ token
     */
    @Override
    public UserImages uploadImages(MultipartFile[] files) throws IOException {
        // Lấy userId từ token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // Tìm thông tin ảnh hiện tại của user
        UserImages userImages = userImagesRepository.findByUserId(userId).orElse(new UserImages());
        userImages.setUserId(userId);

        // Mảng chứa đường dẫn ảnh
        String[] imageFields = {
                userImages.getAnh1(), userImages.getAnh2(), userImages.getAnh3(),
                userImages.getAnh4(), userImages.getAnh5(), userImages.getAnh6(),
                userImages.getAnh7(), userImages.getAnh8(), userImages.getAnh9()
        };

        // Đếm số ảnh đã upload
        int uploadedCount = (int) java.util.Arrays.stream(imageFields).filter(url -> url != null).count();

        if (uploadedCount >= MAX_IMAGES) {
            throw new RuntimeException("Bạn chỉ được tải lên tối đa " + MAX_IMAGES + " ảnh.");
        }

        // Upload ảnh mới và cập nhật vào các vị trí trống
        for (MultipartFile file : files) {
            if (uploadedCount >= MAX_IMAGES) break;

            String imageUrl = uploadSingleImage(file);
            for (int i = 0; i < MAX_IMAGES; i++) {
                if (imageFields[i] == null) {
                    imageFields[i] = imageUrl;
                    uploadedCount++;
                    break;
                }
            }
        }

        // Gán lại vào entity
        userImages.setAnh1(imageFields[0]);
        userImages.setAnh2(imageFields[1]);
        userImages.setAnh3(imageFields[2]);
        userImages.setAnh4(imageFields[3]);
        userImages.setAnh5(imageFields[4]);
        userImages.setAnh6(imageFields[5]);
        userImages.setAnh7(imageFields[6]);
        userImages.setAnh8(imageFields[7]);
        userImages.setAnh9(imageFields[8]);

        return userImagesRepository.save(userImages);
    }
}
