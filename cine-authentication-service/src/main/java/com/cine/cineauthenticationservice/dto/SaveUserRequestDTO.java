package com.cine.cineauthenticationservice.dto;

import com.cine.cineauthenticationservice.enumeration.MileStoneTierCode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class SaveUserRequestDTO extends RegisterRequestDTO{

    public enum Operation {
        CREATE,
        UPDATE
    }

    private Operation operation;
    private Long id;
    private Long tierPoint;
    private MileStoneTierCode tierCode;
}
