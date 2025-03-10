package com.example.mobile.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "profiles")
public class Profile {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String bio;
    private Gender gender;
    private int age;
    private int height;
    private List<Photo> photos;

    private Address address;
}
