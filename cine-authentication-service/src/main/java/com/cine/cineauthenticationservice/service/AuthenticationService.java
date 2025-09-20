package com.cine.cineauthenticationservice.service;

import com.cine.cineauthenticationservice.dto.*;

public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO);

    VerifyResponseDTO verify(VerifyRequestDTO verifyRequestDTO);

    AuthorizeResponseDTO authorize(AuthorizeRequestDTO authorizeRequestDTO);
}
