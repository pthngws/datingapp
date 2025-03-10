package com.example.mobile.dto.request;

import lombok.Data;

@Data
public class ResetPassWordDto {
    private String email;
    private String otp;
    private String newPassword;
}
