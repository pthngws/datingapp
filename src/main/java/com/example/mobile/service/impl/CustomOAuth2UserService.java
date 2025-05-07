package com.example.mobile.service.impl;

import com.example.mobile.dto.response.UserResponse;
import com.example.mobile.exception.AppException;
import com.example.mobile.exception.ErrorCode;
import com.example.mobile.model.User;
import com.example.mobile.service.IAuthenticateService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private IAuthenticateService authenticateService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        try {
            // Sử dụng DefaultOAuth2UserService để tải thông tin người dùng từ provider
            DefaultOAuth2UserService defaultUserService = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = defaultUserService.loadUser(userRequest);

            // Kiểm tra xem có phải OidcUser (Google) hay không
            OidcUser oidcUser = (userRequest instanceof OidcUserRequest && oAuth2User instanceof OidcUser)
                    ? (OidcUser) oAuth2User
                    : null;

            // Gọi oauth2Login để xử lý đăng nhập và lấy UserResponse
            UserResponse userResponse = authenticateService.oauth2Login(oidcUser, oAuth2User);

            // Tạo User từ UserResponse
            User user = new User();
            user.setId(new ObjectId(userResponse.getId()));
            user.setUsername(userResponse.getUsername());
            user.setEmail(userResponse.getEmail());
            user.setRole(userResponse.getRole());

            return new CustomOAuth2User(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Lỗi xác thực OAuth2: " + e.getMessage());
        }
    }
}