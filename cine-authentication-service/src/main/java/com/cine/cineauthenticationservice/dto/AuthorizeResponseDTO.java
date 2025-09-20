package com.cine.cineauthenticationservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class AuthorizeResponseDTO {
    private boolean valid;
}
