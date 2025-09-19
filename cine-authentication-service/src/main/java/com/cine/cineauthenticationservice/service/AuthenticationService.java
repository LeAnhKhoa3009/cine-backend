package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.dto.AuthenticationRequestDTO;
import com.cine.cineauthenticationservice.dto.AuthenticationResponseDTO;
import com.cine.cineauthenticationservice.dto.VerifyResponseDTO;

public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO);

    VerifyResponseDTO verify(String token);
}
