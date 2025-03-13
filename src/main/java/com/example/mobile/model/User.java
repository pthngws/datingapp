package com.example.mobile.model;

import com.example.mobile.model.enums.AccoutStatus;
import com.example.mobile.model.enums.Provider;
import com.example.mobile.model.enums.Role;
import com.example.mobile.model.enums.SubscriptionStatus;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "users")
public class User {
    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String username;

    private String password;
    private String email;
    @CreatedDate
    private LocalDate createAt;
    private Role role;
    private AccoutStatus accountStatus;
    private ObjectId profileId;

    private Provider provider;

    private SubscriptionStatus subscriptionStatus;



}