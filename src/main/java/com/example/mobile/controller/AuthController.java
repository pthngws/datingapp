package com.example.mobile.controller;


import com.example.mobile.dto.IntrospectDto;
import com.example.mobile.dto.request.LoginDto;
import com.example.mobile.dto.request.SignUpDto;
import com.example.mobile.dto.response.ApiResponse;
import com.example.mobile.dto.response.UserDto;
import com.example.mobile.model.User;
import com.example.mobile.service.IAuthenticateService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    @Autowired
    IAuthenticateService authenticateService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@RequestBody SignUpDto signUpDto) {
        try {
            User user = new User();
            user.setUsername(signUpDto.getUsername());
            user.setPassword(signUpDto.getPassword());
            user.setEmail(signUpDto.getEmail());

            User savedUser = authenticateService.signup(user);

            ApiResponse<User> response = new ApiResponse<>(HttpStatus.OK.value(), "Đăng ký thành công", savedUser);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ApiResponse<User> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody LoginDto loginDto) throws JOSEException {
        try {
            UserDto userDto = authenticateService.login(loginDto);

            if (userDto == null) {
                ApiResponse<UserDto> response = new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Tên đăng nhập hoặc mật khẩu không đúng", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            ApiResponse<UserDto> response = new ApiResponse<>(HttpStatus.OK.value(), "Đăng nhập thành công", userDto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            ApiResponse<UserDto> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email != null && !email.isEmpty()) {
            authenticateService.sendOtp(email);
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "OTP đã được gửi đến email của bạn.", null);
            return ResponseEntity.ok(response);
        }
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Email không hợp lệ.", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        if (email != null && otp != null && newPassword != null) {
            boolean success = authenticateService.resetPassword(email, otp, newPassword);
            if (success) {
                ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Mật khẩu đã được đặt lại thành công.", null);
                return ResponseEntity.ok(response);
            }
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "OTP không hợp lệ hoặc email không tồn tại.", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Dữ liệu không hợp lệ.", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<String>> introspect(@RequestBody IntrospectDto token) throws JOSEException, ParseException {
        String email = authenticateService.introspectToken(token);
        if (email != null) {
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.value(), "Token hợp lệ", email);
            return ResponseEntity.ok(response);
        }
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Token không hợp lệ", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
