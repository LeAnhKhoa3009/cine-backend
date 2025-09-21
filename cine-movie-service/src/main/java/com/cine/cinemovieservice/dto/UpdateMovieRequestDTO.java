package com.cine.cinemovieservice.dto;

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
    private String title;
    private String poster;
    private Double rating;
    private LocalDate premiereDate;
    private String description;
    private int duration;
}
