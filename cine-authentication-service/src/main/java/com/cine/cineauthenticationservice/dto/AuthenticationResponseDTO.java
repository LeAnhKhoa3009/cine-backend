package com.cine.cineauthenticationservice.dto;

import com.cine.cineauthenticationservice.enumeration.UserRole;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthenticationResponseDTO {
    private String email;
    private UserRole role;
    private String accessToken;
}
