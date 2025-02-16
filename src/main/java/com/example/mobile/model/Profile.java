package com.example.mobile.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "profiles")
public class Profile {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private boolean gender;
    private int age;
    private int height;
    private String avatar;
    private Address address;
}
