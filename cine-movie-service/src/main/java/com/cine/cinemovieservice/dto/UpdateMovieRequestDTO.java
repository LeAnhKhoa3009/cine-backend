package com.cine.cinemovieservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateMovieRequestDTO {
    private Long id;
    @NotBlank(message = "Title should not be blank")
    private String title;
    private LocalDate premiereDate;
    private String description;
    private int duration;
}
