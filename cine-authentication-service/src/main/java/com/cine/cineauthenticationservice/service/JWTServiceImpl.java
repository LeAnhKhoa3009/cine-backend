package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.config.SecurityConfiguration;
import com.cine.cineauthenticationservice.dto.RetrieveUserReponseDTO;
import com.cine.cineauthenticationservice.enumeration.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
@Log4j2
public class JWTServiceImpl implements JWTService {

    private final SecurityConfiguration securityConfiguration;
    private final Key key;
    private final UserService userService;

    public JWTServiceImpl(SecurityConfiguration securityConfiguration, UserService userService) {
        this.securityConfiguration = securityConfiguration;
        key = Keys.hmacShaKeyFor(securityConfiguration.getSecretKey().getBytes());
        this.userService = userService;
    }

    @Override
    public String generate(String email, UserRole role) {
        Instant expirationDate = Instant.now().plusMillis(Long.parseLong(securityConfiguration.getExpirationTimeInMillis()));
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expirationDate))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    @Override
    public boolean verify(String token) {
        try {
            Jws<Claims> tokenClaim = parseClaims(token);
            Claims claims = tokenClaim.getBody();
            String email = claims.getSubject();

            RetrieveUserReponseDTO user = userService.findByEmail(email);
            if(user != null){
                return user.isActive();
            }

            return false;
        } catch(ExpiredJwtException e) {
            log.error("Token has expired");
            return false;
        } catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }

    @Override
    public boolean authorize(String token, UserRole role) {
        try {
            Jws<Claims> tokenClaim = parseClaims(token);
            Claims claims = tokenClaim.getBody();
            String email = claims.getSubject();
            String roleClaim = claims.get("role", String.class);

            RetrieveUserReponseDTO user = userService.findByEmail(email);
            if(user != null){
                return user.isActive() && user.getRole().name().equals(roleClaim) && user.getRole().equals(role);
            }

            return false;
        } catch(ExpiredJwtException e) {
            log.error("Token has expired");
            return false;
        } catch(JwtException | IllegalArgumentException e){
            return false;
        }
    }
}
