package com.cine.cinemovieservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder public class Movie extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private String poster;

    @Column(nullable = false)
    private Double rating;

    @Column(nullable = false)
    private LocalDate premiereDate;

    @Column(nullable = false)
    private int duration;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieGenre> movieGenres = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new ArrayList<>();
}
