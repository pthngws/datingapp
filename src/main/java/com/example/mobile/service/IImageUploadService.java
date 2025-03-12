package com.example.mobile.service;

import com.example.mobile.model.UserImages;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IImageUploadService {
    String uploadSingleImage(MultipartFile file) throws IOException;

    UserImages uploadImages(MultipartFile[] files) throws IOException;


}
