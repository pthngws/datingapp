package com.example.mobile.service;

import com.example.mobile.model.Album;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IImageUploadService {
    String uploadSingleImage(MultipartFile file) throws IOException;

    Album uploadImages(MultipartFile[] files) throws IOException;


}
