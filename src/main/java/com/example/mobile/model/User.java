package com.example.mobile.model;

import com.example.mobile.model.enums.Provider;
import com.example.mobile.model.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;
    private String password;
    private String email;
    private LocalDate createDate;
    private Role role;
    private String status;
    @DBRef(lazy = true)
    private Profile profile;

    @Enumerated(EnumType.STRING)
    private Provider provider;

}