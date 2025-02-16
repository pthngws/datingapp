package com.example.mobile.controller;


import com.example.mobile.dto.request.LoginDto;
import com.example.mobile.dto.request.SignUpDto;
import com.example.mobile.model.User;
import com.example.mobile.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IUserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody SignUpDto SignUpDto) {
        User user = new User();
        user.setUsername(SignUpDto.getUsername());
        user.setPassword(SignUpDto.getPassword());
        user.setEmail(SignUpDto.getEmail());
        return ResponseEntity.ok(userService.createUser(user));

    }

    @PostMapping("/login")
    public ResponseEntity<Boolean> login(@RequestBody LoginDto loginDto) {
        Optional<User> user = userService.login(loginDto.getUsername(), loginDto.getPassword());
        return ResponseEntity.ok(user.isPresent());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email != null && !email.isEmpty()) {
            userService.sendOtp(email);
            return ResponseEntity.ok("OTP đã được gửi đến email của bạn.");
        }
        return ResponseEntity.badRequest().body("Email không hợp lệ.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        if (email != null && otp != null && newPassword != null) {
            boolean success = userService.resetPassword(email, otp, newPassword);
            return success ? ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.")
                    : ResponseEntity.badRequest().body("OTP không hợp lệ hoặc email không tồn tại.");
        }
        return ResponseEntity.badRequest().body("Dữ liệu không hợp lệ.");
    }

}
