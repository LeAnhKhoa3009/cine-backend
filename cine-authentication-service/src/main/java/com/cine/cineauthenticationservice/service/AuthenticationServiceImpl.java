package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.dto.*;
import com.cine.cineauthenticationservice.validator.EmailValidator;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public AuthenticationServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {
        if (StringUtils.isBlank(authenticationRequestDTO.getEmail())) {
            throw new RuntimeException("Email must not be empty");
        }

        if (!EmailValidator.isValid(authenticationRequestDTO.getEmail())) {
            log.error("Invalid email format");
            throw new IllegalArgumentException("Invalid email format");
        }

        if (StringUtils.isBlank(authenticationRequestDTO.getPassword())) {
            throw new RuntimeException("Password must not be empty");
        }

        RetrieveUserReponseDTO userReponseDTO = userService.findByEmail(authenticationRequestDTO.getEmail());

        if (userReponseDTO == null) {
            log.error("User not found with email: {}", authenticationRequestDTO.getEmail());
            throw new RuntimeException("User not found");
        }

        if(!userReponseDTO.isActive()){
            log.error("User has been deactivated");
            throw new RuntimeException("User has been deactivated");
        }

        if (!passwordEncoder.matches(authenticationRequestDTO.getPassword(), userReponseDTO.getPassword())) {
            log.error("Invalid password for email: {}", authenticationRequestDTO.getEmail());
            throw new RuntimeException("Invalid password");
        }

        return AuthenticationResponseDTO.builder()
                .email(userReponseDTO.getEmail())
                .role(userReponseDTO.getRole())
                .accessToken(jwtService.generate(userReponseDTO.getEmail(), userReponseDTO.getRole()))
                .build();
    }

    @Override
    public VerifyResponseDTO verify(VerifyRequestDTO verifyRequestDTO) {

        if (StringUtils.isBlank(verifyRequestDTO.getAccessToken())) {
            throw new RuntimeException("Token must not be empty");
        }

        return VerifyResponseDTO.builder()
                .valid(jwtService.verify(verifyRequestDTO.getAccessToken()))
                .build();
    }

    @Override
    public AuthorizeResponseDTO authorize(AuthorizeRequestDTO authorizeRequestDTO) {
        if (StringUtils.isBlank(authorizeRequestDTO.getAccessToken())) {
            throw new RuntimeException("Token must not be empty");
        }

        if (authorizeRequestDTO.getRole() == null) {
            throw new RuntimeException("Role must not be empty");
        }

        return AuthorizeResponseDTO.builder()
                .valid(jwtService.authorize(authorizeRequestDTO.getAccessToken(), authorizeRequestDTO.getRole()))
                .build();
    }
}
