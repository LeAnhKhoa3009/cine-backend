package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.enumeration.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface JWTService {
    String generate(String email, UserRole role);

    boolean verify(String token);

    boolean authorize(String token, UserRole role);

    Jws<Claims> parseClaims(String token);
}
