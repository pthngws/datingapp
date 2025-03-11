package com.example.mobile.service.impl;

import com.example.mobile.dto.request.AccessTokenDto;
import com.example.mobile.dto.request.LoginDto;
import com.example.mobile.dto.request.RefreshTokenDto;
import com.example.mobile.dto.response.UserResponse;
import com.example.mobile.exception.AppException;
import com.example.mobile.exception.ErrorCode;
import com.example.mobile.model.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AuthenticateService implements IAuthenticateService {

    @NonFinal
    @Value("${jwt.secret}")
    protected String SECRET;

    @NonFinal
    @Value("${jwt.issuer}")
    protected String ISSUER;

    @Autowired
    private EmailService emailService;

    private HashMap<String, String> otpStorage = new HashMap<>();

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private IUserService userService;

    private static final SecureRandom secureRandom = new SecureRandom();

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String introspectToken(AccessTokenDto token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token.getAccessToken());
        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
        if (signedJWT.verify(verifier) && expirationDate.after(new Date())) {
            return signedJWT.getJWTClaimsSet().getSubject();
        } else {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
    }

    @Override
    public String generateToken(User userEntity) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userEntity.getId())
                .claim("role", "ROLE_" + userEntity.getRole())
                .issuer(ISSUER)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(new MACSigner(SECRET.getBytes()));
        return jwsObject.serialize();
    }

    @Override
    public String generateRefreshToken(User userEntity) {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        long refreshTokenTTL = 7 * 24 * 60 * 60; // 7 ngày
        refreshTokenService.saveRefreshToken(userEntity.getId(), refreshToken, refreshTokenTTL);

        return refreshToken;
    }

    @Override
    public UserResponse oauth2Login(OidcUser oidcUser) throws JOSEException {
        if (oidcUser == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // Tạo tài khoản mới nếu user chưa có trong DB
            user = new User();
            user.setEmail(email);
            user.setUsername(name.replaceAll("\\s", "").toLowerCase());
            user.setRole(Role.USER);
            user.setProvider(Provider.GOOGLE);
            user.setCreateDate(LocalDate.now());
            Profile profile = new Profile();
            profile.setAddress(new Address());
            user.setProfile(profileRepository.save(profile));
            user = userRepository.save(user);
        }

        // Tạo token
        String accessToken = generateToken(user);
        String refreshToken = generateRefreshToken(user);

        return new UserResponse(user, accessToken, refreshToken);
    }


    @Override
    public UserResponse login(LoginDto loginRequestDto) throws JOSEException {
        User user = userService.findByUsername(loginRequestDto.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        String accessToken = this.generateToken(user);
        String refreshToken = this.generateRefreshToken(user);
        UserResponse userResponse = new UserResponse(user);
        userResponse.setToken(accessToken);
        userResponse.setRefreshToken(refreshToken);
        return userResponse;
    }

    @Override
    public User signup(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXIST_REGISTER);
        }
        if (userRepository.existsByEmail(user.getEmail())) { // Giả định có phương thức này
            throw new AppException(ErrorCode.EMAIL_EXIST_REGISTER);
        }
        user.setCreateDate(LocalDate.now());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setProvider(Provider.LOCAL);
        Profile profile = new Profile();
        profile.setAddress(new Address());
        user.setProfile(profileRepository.save(profile));
        return userRepository.save(user);
    }

    @Override
    public void sendOtp(String email) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStorage.put(email, otp);
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public boolean resetPassword(String email, String otp, String newPassword) {
        if (!otpStorage.containsKey(email)) {
            throw new AppException(ErrorCode.OTP_NOT_FOUND);
        }
        if (!otpStorage.get(email).equals(otp)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        otpStorage.remove(email);
        return true;
    }

    @Override
    public String refreshAccessToken(RefreshTokenDto refreshToken) throws JOSEException {
        String userId = getUserIdFromRefreshToken(refreshToken);
        if (userId == null) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        return generateToken(user);
    }

    private String getUserIdFromRefreshToken(RefreshTokenDto refreshToken) {
        // Duyệt qua tất cả user để tìm refresh token (Không tối ưu)
        for (User user : userRepository.findAll()) {
            String storedToken = refreshTokenService.getRefreshToken(user.getId());
            if (refreshToken.getRefreshToken().equals(storedToken)) {
                return user.getId();
            }
        }
        return null;
    }

    @Override
    public void revokeRefreshToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userId = authentication.getName();
        refreshTokenService.deleteRefreshToken(userId);
    }
}