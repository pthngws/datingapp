package com.example.mobile.controller;

import com.example.mobile.dto.request.*;
import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.dto.response.UserResponse;
import com.example.mobile.model.User;
import com.example.mobile.service.IAuthenticateService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Các API liên quan đến xác thực người dùng")
public class AuthController {

    @Autowired
    IAuthenticateService authenticateService;

    @Operation(summary = "Đăng ký tài khoản", description = "Tạo tài khoản mới với thông tin username, password, email")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody SignUpDto signUpDto) {
        User savedUser = authenticateService.signup(signUpDto);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Đăng ký thành công", savedUser));
    }

    @Operation(summary = "Đăng nhập", description = "Người dùng đăng nhập với username và password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginDto loginDto) throws JOSEException {
        UserResponse userResponse = authenticateService.login(loginDto);
        if (userResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Tên đăng nhập hoặc mật khẩu không đúng", null));
        }
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Đăng nhập thành công", userResponse));
    }

    @Operation(summary = "Đăng nhập Google hoặc Facebook", description = "Người dùng đăng nhập bằng Google hoặc Facebook OAuth2")
    @GetMapping("/oauth2-login")
    public ResponseEntity<ApiResponse<UserResponse>> oauth2Login(
            @AuthenticationPrincipal OidcUser oidcUser,
            @AuthenticationPrincipal OAuth2User oAuth2User) throws JOSEException {

        UserResponse userResponse = authenticateService.oauth2Login(oidcUser, oAuth2User);
        if (userResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Không thể xác thực bằng Google hoặc Facebook", null)
            );
        }
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Đăng nhập thành công", userResponse));
    }



    @Operation(summary = "Đăng xuất", description = "Xóa refresh token của người dùng")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        authenticateService.revokeRefreshToken();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Đăng xuất thành công", null));
    }

    @Operation(summary = "Làm mới token", description = "Nhận access token mới bằng refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestBody RefreshTokenDto refreshToken) throws JOSEException, ParseException {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Refresh token không được cung cấp", null));
        }
        String newAccessToken = authenticateService.refreshAccessToken(refreshToken);
        if (newAccessToken != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Access token mới đã được tạo", newAccessToken));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Refresh token không hợp lệ hoặc đã hết hạn", null));
    }

    @Operation(summary = "Quên mật khẩu", description = "Gửi OTP đến email của người dùng")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ForgotPassWordDto forgotPasswordDto) {
        String email = forgotPasswordDto.getEmail();
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Email không hợp lệ.", null));
        }
        authenticateService.sendOtp(email);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "OTP đã được gửi đến email của bạn.", null));
    }

    @Operation(summary = "Đặt lại mật khẩu", description = "Người dùng đặt lại mật khẩu bằng OTP đã nhận")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPassWordDto resetPasswordDto) {
        String email = resetPasswordDto.getEmail();
        String otp = resetPasswordDto.getOtp();
        String newPassword = resetPasswordDto.getNewPassword();

        if (email == null || otp == null || newPassword == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Dữ liệu không hợp lệ.", null));
        }

        boolean success = authenticateService.resetPassword(email, otp, newPassword);
        if (success) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Mật khẩu đã được đặt lại thành công.", null));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "OTP không hợp lệ hoặc email không tồn tại.", null));
    }

    @Operation(summary = "Kiểm tra token", description = "Xác thực token có hợp lệ hay không")
    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<String>> introspect(@RequestBody AccessTokenDto token) throws JOSEException, ParseException {
        String email = authenticateService.introspectToken(token);
        if (email != null) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Token hợp lệ", email));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Token không hợp lệ", null));
    }
}
