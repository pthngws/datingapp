package com.example.mobile.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF (chỉ dùng nếu API không cần)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/signup", "/api/auth/login","/api/profiles/**", "/api/auth/reset-password").permitAll() // Cho phép truy cập API đăng ký mà không cần xác thực
                        .anyRequest().authenticated() // Các API khác yêu cầu đăng nhập
                )
                .formLogin(form -> form.disable()) // Tắt form login mặc định của Spring Security
                .httpBasic(basic -> basic.disable()); // Tắt HTTP Basic Auth nếu không dùng

        return http.build();
    }
}