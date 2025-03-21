package com.example.mobile.config;

import com.example.mobile.service.impl.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/signup/request",
            "/api/auth/signup/verify",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/auth/reset-password",
            "/api/auth/forgot-password",
            "/api/auth/refresh-token",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/api/auth/oauth2-login"
    };
    private final String[] ADMIN_ENDPOINTS = { "/api/admin/**" };
    private final String[] USER_ENDPOINTS = { };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, JwtDecoder jwtDecoder, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        httpSecurity
                .authorizeHttpRequests(request -> request
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(USER_ENDPOINTS).hasRole("USER")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/auth/oauth2-login", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(new CustomOAuth2UserService()) // Hỗ trợ cả Google và Facebook
                        )
                )
                .csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
}