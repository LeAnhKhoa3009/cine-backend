package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.dto.AuthenticationRequestDTO;
import com.cine.cineauthenticationservice.dto.AuthenticationResponseDTO;
import com.cine.cineauthenticationservice.dto.RetrieveUserReponseDTO;
import com.cine.cineauthenticationservice.dto.VerifyResponseDTO;
import com.cine.cineauthenticationservice.validator.EmailValidator;
import com.cine.cineauthenticationservice.validator.PasswordValidator;
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
                .accessToken(jwtService.generateToken(userReponseDTO.getEmail()))
                .build();
    }

    @Override
    public VerifyResponseDTO verify(String token) {
        if(StringUtils.isBlank(token)){
            log.error("Token is empty");
            throw new RuntimeException("Token is empty");
        }

        return VerifyResponseDTO.builder()
                .valid(jwtService.verifyToken(token))
                .build();
    }
}
