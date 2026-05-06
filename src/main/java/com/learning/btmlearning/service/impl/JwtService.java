package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.access-expiration}")
    @NonFinal
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    @NonFinal
    private Long refreshExpiration;

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessExpiration, ChronoUnit.MINUTES);

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS512).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("BTM-learning")
                .subject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .claim("jti", UUID.randomUUID().toString())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(refreshExpiration, ChronoUnit.MINUTES);

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS512).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("BTM-learning")
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("type", "refresh")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String extractUsername(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    public String extractUserId(String token) {
        return jwtDecoder.decode(token).getClaimAsString("userId");
    }

    public String extractJti(String token) {
        return jwtDecoder.decode(token).getClaimAsString("jti");
    }

    public String extractType(String token) {
        return jwtDecoder.decode(token).getClaimAsString("type");
    }


    public Instant extractExpiration(String token) {
        return jwtDecoder.decode(token).getExpiresAt();
    }

    public long getExpiryDuration(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        Instant expiresAt = jwt.getExpiresAt();
        return Duration.between(Instant.now(), expiresAt).toMillis();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String jti = jwt.getClaimAsString("jti");

            String blackListToken = (String) redisTemplate.opsForValue().get("blacklist:" + jti);

            if (Objects.nonNull(blackListToken)) {
                throw new RuntimeException("Token is blacklisted");
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}