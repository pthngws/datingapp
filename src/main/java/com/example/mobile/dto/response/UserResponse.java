package com.example.mobile.dto.response;

import com.example.mobile.model.User;
import com.example.mobile.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private Role role;
    private String token;
    private String refreshToken;

    public UserResponse(User user) {
        this.id = user.getId().toHexString();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
    }

    public UserResponse(User user,String token,String refreshToken) {
        this.id = user.getId().toHexString();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.refreshToken = refreshToken;
        this.token = token;
    }
}