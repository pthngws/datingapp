package com.example.mobile.service.impl;

import com.example.mobile.dto.request.*;
import com.example.mobile.dto.response.UserResponse;
import com.example.mobile.exception.AppException;
import com.example.mobile.exception.ErrorCode;
import com.example.mobile.model.Address;
import com.example.mobile.model.Album;
import com.example.mobile.model.Profile;
import com.example.mobile.model.User;
import com.example.mobile.model.enums.AccoutStatus;
import com.example.mobile.model.enums.Provider;
import com.example.mobile.model.enums.Role;
import com.example.mobile.model.enums.SubscriptionStatus;
import com.example.mobile.repository.AddressRepository;
import com.example.mobile.repository.AlbumRepository;
import com.example.mobile.repository.ProfileRepository;
import com.example.mobile.repository.UserRepository;
import com.example.mobile.service.IAuthenticateService;
import com.example.mobile.service.IUserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private HashMap<String, SignUpDto> pendingSignups = new HashMap<>(); // Lưu tạm thông tin đăng ký

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private IUserService userService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AlbumRepository albumRepository;

    private static final SecureRandom secureRandom = new SecureRandom();

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void requestSignup(SignUpDto signUpDto) {
        // Kiểm tra username và email đã tồn tại chưa
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXIST_REGISTER);
        }
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXIST_REGISTER);
        }

        // Tạo và gửi OTP
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStorage.put(signUpDto.getEmail(), otp);
        pendingSignups.put(signUpDto.getEmail(), signUpDto); // Lưu thông tin đăng ký tạm thời
        emailService.sendOtpEmail(signUpDto.getEmail(), otp);
    }

    @Override
    public User verifySignup(VerifySignUpDto verifySignUpDto) {
        String email = verifySignUpDto.getEmail();

        // Kiểm tra OTP
        if (!otpStorage.containsKey(email) || !otpStorage.get(email).equals(verifySignUpDto.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        // Kiểm tra thông tin đăng ký tạm thời
        SignUpDto signUpDto = pendingSignups.get(email);
        if (signUpDto == null || !signUpDto.getUsername().equals(verifySignUpDto.getUsername())
                || !signUpDto.getPassword().equals(verifySignUpDto.getPassword())) {
            throw new AppException(ErrorCode.INVALID_SIGNUP_DATA);
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setEmail(signUpDto.getEmail());
        user.setCreateAt(LocalDate.now());
        user.setRole(Role.USER);
        user.setProvider(Provider.LOCAL);
        user.setAccountStatus(AccoutStatus.ACTIVE);
        user.setSubscriptionStatus(SubscriptionStatus.FREE);

        // Tạo profile, address, album
        Profile profile = new Profile();
        Address address = new Address();
        addressRepository.save(address);
        Album album = new Album();
        albumRepository.save(album);
        profile.setAddressId(address.getId());
        profile.setAlbumId(album.getId());
        profileRepository.save(profile);
        user.setProfileId(profile.getId());

        // Lưu user và xóa dữ liệu tạm
        User savedUser = userRepository.save(user);
        otpStorage.remove(email);
        pendingSignups.remove(email);

        return savedUser;
    }

    @Override
    public UserResponse login(LoginDto loginRequestDto) throws JOSEException {
        // Nếu là đăng nhập bằng Google
        if (loginRequestDto.isGoogleLogin()) {
            String email = loginRequestDto.getUsername(); // Email từ Google

            // Tìm user bằng email
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user = existingUser.orElseGet(() -> {
                // Tạo user mới nếu chưa tồn tại
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(email.split("@")[0]); // Tạo username từ phần đầu email
                newUser.setRole(Role.USER);
                newUser.setProvider(Provider.GOOGLE);
                newUser.setCreateAt(LocalDate.now());
                newUser.setAccountStatus(AccoutStatus.ACTIVE);
                newUser.setSubscriptionStatus(SubscriptionStatus.FREE);

                // Tạo profile, address, album
                Profile profile = new Profile();
                Address address = new Address();
                addressRepository.save(address);
                Album album = new Album();
                albumRepository.save(album);
                profile.setAddressId(address.getId());
                profile.setAlbumId(album.getId());
                profileRepository.save(profile);
                newUser.setProfileId(profile.getId());

                return userRepository.save(newUser);
            });

            if (user.getAccountStatus() != AccoutStatus.ACTIVE) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            String accessToken = this.generateToken(user);
            String refreshToken = this.generateRefreshToken(user);
            UserResponse userResponse = new UserResponse(user);
            userResponse.setToken(accessToken);
            userResponse.setRefreshToken(refreshToken);
            return userResponse;
        }
        // Đăng nhập thông thường
        else {
            User user = userService.findByUsername(loginRequestDto.getUsername());
            if (user == null || !passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
                throw new AppException(ErrorCode.USER_NOT_EXIST);
            }
            if (user.getAccountStatus() != AccoutStatus.ACTIVE) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
            String accessToken = this.generateToken(user);
            String refreshToken = this.generateRefreshToken(user);
            UserResponse userResponse = new UserResponse(user);
            userResponse.setToken(accessToken);
            userResponse.setRefreshToken(refreshToken);
            return userResponse;
        }
    }

    @Override
    public UserResponse oauth2Login(OidcUser oidcUser, OAuth2User oAuth2User) throws JOSEException {
        if (oidcUser == null && oAuth2User == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String email, name, provider;
        if (oidcUser != null) {
            // Xác thực Google
            email = oidcUser.getEmail();
            name = oidcUser.getFullName();
            provider = "GOOGLE";
        } else {
            // Xác thực Facebook
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
            provider = "FACEBOOK";
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        User user = existingUser.orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name.replaceAll("\\s", "").toLowerCase());
            newUser.setRole(Role.USER);
            newUser.setProvider(provider.equals("FACEBOOK") ? Provider.FACEBOOK : Provider.GOOGLE);
            newUser.setCreateAt(LocalDate.now());
            newUser.setAccountStatus(AccoutStatus.ACTIVE);
            newUser.setSubscriptionStatus(SubscriptionStatus.FREE);
            Profile profile = new Profile();
            Address address = new Address();
            addressRepository.save(address);
            Album album = new Album();
            albumRepository.save(album);
            profile.setAddressId(address.getId());
            profile.setAlbumId(album.getId());
            profileRepository.save(profile);
            newUser.setProfileId(profile.getId());

            return userRepository.save(newUser);
        });
        if(user.getAccountStatus() != AccoutStatus.ACTIVE) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String accessToken = generateToken(user);
        String refreshToken = generateRefreshToken(user);

        return new UserResponse(user, accessToken, refreshToken);
    }

    @Override
    public User findOrCreateUser(String email, String name, String provider) {
        return null;
    }

    @Override
    public UserResponse googleLogin(String idToken) throws JOSEException {
        try {
            // Xác minh Google ID Token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList("682947406441-00mqnufgm7kv6ucgccf9alreitg5dp08.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String provider = "GOOGLE";

            // Tìm hoặc tạo người dùng
            Optional<User> existingUser = userRepository.findByEmail(email);
            User user = existingUser.orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(name != null ? name.replaceAll("\\s", "").toLowerCase() : email.split("@")[0]);
                newUser.setRole(Role.USER);
                newUser.setProvider(Provider.GOOGLE);
                newUser.setCreateAt(LocalDate.now());
                newUser.setAccountStatus(AccoutStatus.ACTIVE);
                newUser.setSubscriptionStatus(SubscriptionStatus.FREE);

                // Tạo profile, address, album
                Profile profile = new Profile();
                Address address = new Address();
                addressRepository.save(address);
                Album album = new Album();
                albumRepository.save(album);
                profile.setAddressId(address.getId());
                profile.setAlbumId(album.getId());
                profileRepository.save(profile);
                newUser.setProfileId(profile.getId());

                return userRepository.save(newUser);
            });

            if (user.getAccountStatus() != AccoutStatus.ACTIVE) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            // Tạo token
            String accessToken = generateToken(user);
            String refreshToken = generateRefreshToken(user);

            return new UserResponse(user, accessToken, refreshToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Lỗi xác thực Google ID Token: " + e.getMessage());
        }
    }

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
                .subject(String.valueOf(userEntity.getId()))
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
    public String refreshAccessToken(RefreshTokenDto refreshToken) throws JOSEException {
        ObjectId userId = getUserIdFromRefreshToken(refreshToken);
        if (userId == null) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        return generateToken(user);
    }

    @Override
    public void revokeRefreshToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        ObjectId userId = new ObjectId(authentication.getName());
        refreshTokenService.deleteRefreshToken(userId);
    }
    private ObjectId getUserIdFromRefreshToken(RefreshTokenDto refreshToken) {
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
    public void sendOtp(ForgotPassWordDto forgotPassWordDto) {
        String email = forgotPassWordDto.getEmail();
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStorage.put(email, otp);
        emailService.sendOtpEmail(email, otp);
    }

    @Override
    public boolean resetPassword(ResetPasswordDto resetPassWordDto) {
        String email = resetPassWordDto.getEmail();
        String otp = resetPassWordDto.getOtp();
        String newPassword = resetPassWordDto.getNewPassword();
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


}