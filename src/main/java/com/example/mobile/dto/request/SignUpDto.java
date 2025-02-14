package com.example.mobile.dto.request;

import lombok.Data;

@Data
public class SignUpDto {
    private String username;
    private String password;
    private String email;
}
