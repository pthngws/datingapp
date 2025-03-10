package com.example.mobile.service;

import com.example.mobile.dto.request.IntrospectDto;
import com.example.mobile.dto.request.LoginDto;
import com.example.mobile.dto.response.UserResponse;
import com.example.mobile.model.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface IAuthenticateService {
    String introspectToken(IntrospectDto token) throws JOSEException, ParseException;

    String generateToken(User userEntity) throws JOSEException;

    UserResponse login(LoginDto loginRequestDto) throws JOSEException;

    User signup(User user);

    void sendOtp(String email);

    boolean resetPassword(String email, String otp, String newPassword);

    String refreshAccessToken(String refreshToken) throws JOSEException, ParseException;

    void revokeRefreshToken();
}
