package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.config.SecurityConfiguration;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JWTServiceImpl implements JWTService{

    private final SecurityConfiguration securityConfiguration;
    private final Key key;

    public JWTServiceImpl(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
        key = Keys.hmacShaKeyFor(securityConfiguration.getSecretKey().getBytes());
    }

    @Override
    public String generateToken(String email) {
        Instant expirationDate = Instant.now().plusMillis(Long.parseLong(securityConfiguration.getExpirationTimeInMillis()));
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expirationDate))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public String extractEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean verifyToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
