package com.example.mobile.service.impl;

import com.example.mobile.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final User user;

    public CustomOAuth2User(User user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", user.getId().toHexString());
        attributes.put("username", user.getUsername());
        attributes.put("email", user.getEmail());
        attributes.put("role", user.getRole().name());
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(() -> "ROLE_" + user.getRole().name());
    }

    @Override
    public String getName() {
        return user.getUsername();
    }
}