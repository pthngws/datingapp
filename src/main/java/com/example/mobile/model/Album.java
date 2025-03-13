package com.example.mobile.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "album")
public class Album {
    @Id
    private ObjectId id;

    private String pic1;
    private String pic2;
    private String pic3;
    private String pic4;
    private String pic5;
    private String pic6;
    private String pic7;
    private String pic8;
    private String pic9;
}