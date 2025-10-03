package com.cine.cineauthenticationservice.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetrieveUserTierResponseDTO {
    private String name;
    private String code;
    private Long requiredPoints;
}
