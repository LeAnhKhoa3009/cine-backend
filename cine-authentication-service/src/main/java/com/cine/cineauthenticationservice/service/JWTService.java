package com.cine.cineauthenticationservice.service;

public interface JWTService {
    String generateToken(String email);
    boolean verifyToken(String token);
    String extractEmailFromToken(String token);
}
