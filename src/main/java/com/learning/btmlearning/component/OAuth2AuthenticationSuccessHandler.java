package com.learning.btmlearning.component;

import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.repository.UserRepository;
import com.learning.btmlearning.service.impl.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    JwtService jwtService;
    UserRepository userRepository;
    RedisTemplate<String, Object> redisTemplate;

    @Value("${front-end.oauth2-redirect-url:http://localhost:5173/oauth2/redirect}")
    @NonFinal
    String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email).orElseThrow(
               () ->new RuntimeException("lỗi roiiiii")
        );

        // 1. Tạo bộ đôi Token nhà mình
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        long refreshTokenTtlMs = Math.max(jwtService.getExpiryDuration(refreshToken), 0);

        if (refreshTokenTtlMs > 0) {
            redisTemplate.opsForValue().set(
                    "refresh:" + user.getId(),
                    refreshToken,
                    Duration.ofMillis(refreshTokenTtlMs)
            );
        }

        // 2. Điều hướng về Frontend kèm Token trên URL
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("token", accessToken)
                .queryParam("refresh_token", refreshToken)
                .build().toUriString();
//        String targetUrl = UriComponentsBuilder.fromUriString("https://jwt.io")
//                .queryParam("token", accessToken)
//                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
