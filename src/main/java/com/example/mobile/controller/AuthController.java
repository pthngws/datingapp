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

}
