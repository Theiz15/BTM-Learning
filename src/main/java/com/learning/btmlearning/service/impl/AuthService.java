package com.learning.btmlearning.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.learning.btmlearning.constant.Provider;
import com.learning.btmlearning.constant.UserRole;
import com.learning.btmlearning.dto.request.GoogleLoginRequest;
import com.learning.btmlearning.dto.request.LoginRequest;
import com.learning.btmlearning.dto.request.RefreshRequest;
import com.learning.btmlearning.dto.request.RegisterRequest;
import com.learning.btmlearning.dto.response.AuthResponse;
import com.learning.btmlearning.dto.response.UserProfile;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.mapper.UserMapper;
import com.learning.btmlearning.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
@Slf4j
public class AuthService{
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    UserMapper userMapper ;
    RedisTemplate<String, Object> redisTemplate;

    AuthenticationManager authenticationManager;

    @Value("${jwt.access-expiration}")
    @NonFinal
    Long accessExpiration;

    @NonFinal
    @Value("${jwt.refresh-expiration}")
    Long refreshExpiration;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    @NonFinal
    String googleClientId;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(UserRole.STUDENT)
                .provider(Provider.LOCAL)
                .isActive(true)
                .build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user.getId(), refreshToken);

        return buildAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        log.info(">>> User {} đã đăng nhập thành công qua Spring Security!", request.getEmail());
         User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                 () -> new AppException(ErrorCode.USER_NOT_EXISTED)
         );

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        if(!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED) ;
        }

         String accessToken = jwtService.generateAccessToken(user);
         String refreshToken = jwtService.generateRefreshToken(user);
        saveRefreshToken(user.getId(), refreshToken);
         return buildAuthResponse(user, accessToken, refreshToken);
    }

    public AuthResponse refreshToken(RefreshRequest request) {
        if (!jwtService.isTokenValid(request.getRefreshToken())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        if (!"refresh".equals(jwtService.extractType(request.getRefreshToken()))) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        String userIdStr = jwtService.extractUserId(request.getRefreshToken());
        Long userId = Long.parseLong(userIdStr);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String storedRefreshToken = (String) redisTemplate.opsForValue().get("refresh:" + userId);

        if (storedRefreshToken == null || !storedRefreshToken.equals(request.getRefreshToken())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }



        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                newRefreshToken,
                Duration.ofDays(7)
        );

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }

        String token = authHeader.substring(7);

        String jti = jwtService.extractJti(token);
        String userId = jwtService.extractUserId(token);

        long expiryDuration = jwtService.getExpiryDuration(token);

        if (expiryDuration > 0) {
            redisTemplate.opsForValue().set("blacklist:" + jti, "true", Duration.ofMillis(expiryDuration));
        }

        redisTemplate.delete("refresh:" + userId);

        SecurityContextHolder.clearContext();
    }

    public AuthResponse googleLogin(GoogleLoginRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken()) ;

            if (idToken == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String nickname = (String) payload.get("name");
            String avatarUrl = (String) payload.get("picture");

            User user = userRepository.findByEmail(email).orElseGet(() ->{
                User newUser = User.builder()
                        .email(email)
                        .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                        .fullName(nickname)
                        .role(UserRole.STUDENT)
                        .provider(Provider.GOOGLE)
                        .isActive(true)
                        .avatarUrl(avatarUrl)
                        .build();
                return userRepository.save(newUser);
                    }
            );

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            saveRefreshToken(user.getId(), refreshToken);

            log.info(">>> Đăng nhập Google thành công cho email: {}", email);
            return buildAuthResponse(user, accessToken, refreshToken);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthResponse buildAuthResponse(User user, String accessToken , String refreshToken) {
        UserProfile profile = userMapper.toUserProfile(user);

        return new AuthResponse(accessToken, refreshToken,  accessExpiration, profile);
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                "refresh:" + userId,
                refreshToken,
                Duration.ofSeconds(refreshExpiration)
        );
    }
}
