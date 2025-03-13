package com.example.mobile.model;


import com.example.mobile.model.enums.Gender;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "filters")
public class Filter {
    @Id
    private ObjectId id;
    private String userId;
    private Gender gender;
    private int minAge;
    private int maxAge;
    private Address address;
}
