package com.nocountry.fintech.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long jwtExpirationMs;

    public JwtService(
            @Value("${jwt.secret:ClaveSecretaPorDefectoParaDesarrolloLocal2026}") String secret,
            @Value("${jwt.expiration:86400000}") long jwtExpirationMs
    ) {
        this.jwtExpirationMs = jwtExpirationMs;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtExpirationMs))
                .signWith(secretKey)
                .compact();
    }
}
