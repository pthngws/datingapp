package com.example.mobile.config;

import com.example.mobile.model.Role;
import com.example.mobile.model.User;
import com.example.mobile.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    ApplicationRunner init(UserRepository userRepository){
        return args -> {
            if(userRepository.findByUsername("admin")==null)
            {
                User userDto = new User();
                userDto.setUsername("admin");
                userDto.setEmail("admin@admin");
                userDto.setPassword(passwordEncoder.encode("admin@admin"));
                userDto.setRole(Role.ADMIN);
                userRepository.save(userDto);
                log.warn("Admin created");
            }
        };
    }
}