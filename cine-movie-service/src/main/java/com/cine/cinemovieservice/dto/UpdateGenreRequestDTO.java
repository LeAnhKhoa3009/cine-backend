package com.cine.cinemovieservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateGenreRequestDTO {

    private Long id;

    @NotBlank(message = "Updated gerne name should not be blank")
    private String name;

    private String icon;
}
