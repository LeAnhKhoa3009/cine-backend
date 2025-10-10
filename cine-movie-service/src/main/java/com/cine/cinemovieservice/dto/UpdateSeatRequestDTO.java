package com.cine.cinemovieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSeatRequestDTO {
    private Long id;
    private Boolean premium;
    private Boolean empty;
}
