package com.example.mobile.service.impl;


import com.example.mobile.dto.IntrospectDto;
import com.example.mobile.dto.request.LoginDto;
import com.example.mobile.dto.response.UserDto;
import com.example.mobile.model.Address;
import com.example.mobile.model.Profile;
import com.example.mobile.model.Role;
import com.example.mobile.model.User;
import com.example.mobile.repository.ProfileRepository;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.service.IAuthenticateService;
import com.example.mobile.service.IUserService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticateService implements IAuthenticateService {

    @NonFinal
    @Value("${jwt.secret}")
    protected String SECRET;

    @Autowired
    private EmailService emailService;

    private HashMap<String, String> otpStorage = new HashMap<>();

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IUserService userService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String introspectToken(IntrospectDto token) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(SECRET.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token.getToken());
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (signedJWT.verify(verifier) && expirationDate.after(new Date())) {
            return signedJWT.getJWTClaimsSet().getSubject();
        } else {
            return null;
        }
    }

    @Override
    public String generateToken(User userEntity) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userEntity.getId()) // Lưu userId trong "sub"
                .claim("role", "ROLE_" + userEntity.getRole())
                .issuer("http://localhost:8080")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(new MACSigner(SECRET.getBytes()));

        return jwsObject.serialize();
    }


    @Override
    public UserDto login(LoginDto loginRequestDto) throws JOSEException {
        User user = userService.findByUsername(loginRequestDto.getUsername());
        if (user != null) {
            if (passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
                String token = this.generateToken(user);
                UserDto userDto = new UserDto(user);
                userDto.setToken(token);
                return userDto;
            }

        }
        return null;
    }

    @Override
    public User signup(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại");
        }
        try {
            user.setCreateDate(LocalDate.now());
            user.setRole(Role.USER);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Profile profile = new Profile();
            profile.setAddress(new Address());
            user.setProfile(profileRepository.save(profile));
            return userRepository.save(user);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
    }

    @Override
    public void sendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStorage.put(email, otp);

        // Gửi OTP qua email
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public boolean resetPassword(String email, String otp, String newPassword) {
        if (otpStorage.containsKey(email) && otpStorage.get(email).equals(otp)) {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                otpStorage.remove(email);
                return true;
            }
        }
        return false;
    }

}