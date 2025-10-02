package com.cine.cinemovieservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ImportMovieDTO {
    private Long id;

    private String title;

    private String description;

    private String poster;

    private Double rating;

    private LocalDate premiereDate;

    private int duration;

    private List<Long> genres;
}
