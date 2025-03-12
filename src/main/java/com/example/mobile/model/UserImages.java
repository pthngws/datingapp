package com.example.mobile.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user_images")
public class UserImages {
    @Id
    private String id; // MongoDB dùng String cho Id thay vì Long

    @Indexed(unique = true) // Đảm bảo userId là duy nhất
    private String userId;

    private String anh1;
    private String anh2;
    private String anh3;
    private String anh4;
    private String anh5;
    private String anh6;
    private String anh7;
    private String anh8;
    private String anh9;
}