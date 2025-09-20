package com.cine.cinedirectapi.dto;

import com.cine.cinedirectapi.enumeration.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorizeRequestDTO {
    private UserRole role;
    private String accessToken;
}
