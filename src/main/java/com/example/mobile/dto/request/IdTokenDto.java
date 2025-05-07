package com.example.mobile.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdTokenDto {
    @NotBlank(message = "ID Token không được để trống")
    private String idToken;
}