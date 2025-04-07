package com.example.mobile.dto.response;

import lombok.Data;

@Data
public class UserInfoResponse {
    private String userId;
    private int age;
    private String firstName;
    private String lastName;
    private String pic1;
}
