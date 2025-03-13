package com.example.mobile.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.mobile.model.Album;
import com.example.mobile.repository.AlbumRepository;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.service.IImageUploadService;
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

    private static final int MAX_IMAGES = 9;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String uploadSingleImage(MultipartFile file) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }

    @Override
    public Album uploadImages(MultipartFile[] files) throws IOException {
        // Lấy userId từ token
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // Tìm thông tin ảnh hiện tại của user
        Album userImages = userRepository.findById(userId).get().getProfile().getAlbum();

        // Mảng chứa đường dẫn ảnh
        String[] imageFields = {
                userImages.getPic1(), userImages.getPic2(), userImages.getPic3(),
                userImages.getPic4(), userImages.getPic5(), userImages.getPic6(),
                userImages.getPic7(), userImages.getPic8(), userImages.getPic9()
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
        userImages.setPic1(imageFields[0]);
        userImages.setPic2(imageFields[1]);
        userImages.setPic3(imageFields[2]);
        userImages.setPic4(imageFields[3]);
        userImages.setPic5(imageFields[4]);
        userImages.setPic6(imageFields[5]);
        userImages.setPic7(imageFields[6]);
        userImages.setPic8(imageFields[7]);
        userImages.setPic9(imageFields[8]);

        return albumRepository.save(userImages);
    }
}
