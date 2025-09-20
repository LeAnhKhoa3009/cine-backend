package com.cine.cineauthenticationservice.dto;

import com.cine.cineauthenticationservice.enumeration.UserRole;
import lombok.Data;

@Data
public class AuthorizeRequestDTO {
    private UserRole role;
    private String accessToken;
}
