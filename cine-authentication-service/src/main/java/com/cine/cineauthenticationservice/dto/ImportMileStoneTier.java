package com.cine.cineauthenticationservice.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ImportMileStoneTier {
    private Long id;
    private String name;
    private String code;
    private Long requiredPoints;
}
