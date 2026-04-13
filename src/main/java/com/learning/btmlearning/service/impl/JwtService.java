package com.learning.btmlearning.service.impl;

import com.learning.btmlearning.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {

    JwtEncoder jwtEncoder;
    JwtDecoder jwtDecoder;

    @Value("${jwt.access-expiration}")
    @NonFinal
    Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    @NonFinal
    Long refreshExpiration;

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessExpiration);

        // BẮT BUỘC: Chỉ định thuật toán trong Header
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

        // Truyền cả header và claims
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(refreshExpiration);

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
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}