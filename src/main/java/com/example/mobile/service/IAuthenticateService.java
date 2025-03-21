package com.example.mobile.service;

import com.example.mobile.dto.request.*;
import com.example.mobile.dto.response.UserResponse;
import com.example.mobile.model.User;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.text.ParseException;

public interface IAuthenticateService {

    void requestSignup(SignUpDto signUpDto);

    User verifySignup(VerifySignUpDto verifySignUpDto);  UserResponse login(LoginDto loginRequestDto) throws JOSEException;
    UserResponse oauth2Login(OidcUser oidcUser, OAuth2User oAuth2User) throws JOSEException;

    String generateToken(User userEntity) throws JOSEException;
    String generateRefreshToken(User userEntity);
    String introspectToken(AccessTokenDto token) throws JOSEException, ParseException;
    String refreshAccessToken(RefreshTokenDto refreshToken) throws JOSEException, ParseException;
    void revokeRefreshToken();

    void sendOtp(ForgotPassWordDto forgotPassWordDto);
    boolean resetPassword(ResetPasswordDto resetPassWordDto);
}
