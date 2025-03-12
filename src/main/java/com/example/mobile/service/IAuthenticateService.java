package com.example.mobile.service;

import com.example.mobile.dto.request.AccessTokenDto;
import com.example.mobile.dto.request.LoginDto;
import com.example.mobile.dto.request.RefreshTokenDto;
import com.example.mobile.dto.response.UserResponse;
import com.example.mobile.model.User;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.text.ParseException;

public interface IAuthenticateService {
    String introspectToken(AccessTokenDto token) throws JOSEException, ParseException;

    String generateToken(User userEntity) throws JOSEException;

    String generateRefreshToken(User userEntity);


    UserResponse oauth2Login(OidcUser oidcUser, OAuth2User oAuth2User) throws JOSEException;

    UserResponse login(LoginDto loginRequestDto) throws JOSEException;

    User signup(User user);

    void sendOtp(String email);

    boolean resetPassword(String email, String otp, String newPassword);

    String refreshAccessToken(RefreshTokenDto refreshToken) throws JOSEException, ParseException;

    void revokeRefreshToken();
}
