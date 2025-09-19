package com.cine.cineauthenticationservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RegisterResponseDTO {
    private Long id;
    private String email;
}
