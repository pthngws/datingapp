package com.example.mobile.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    private String password;

    private boolean isGoogleLogin = false;
}
