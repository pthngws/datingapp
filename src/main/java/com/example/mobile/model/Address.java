package com.example.mobile.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "addresses")
public class Address {
    @Id
    private String id;
    private String street;
    private String district;
    private String province;
}
