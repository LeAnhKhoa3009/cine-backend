package com.cine.cinemovieservice.dto;

import com.cine.cinemovieservice.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MovieResponseDTO {
        private Long id;
        private String title;
        private String description;
        private String poster;
        private Double rating;
        private LocalDate premiereDate;
        private int duration;
        private Set<Genre> genres;
        private boolean deleted;
        private String teaser;
}
