package com.example.mobile.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "addresses")
public class Address {
    @Id
    private ObjectId id;
    private String street;
    private String district;
    private String province;
}
