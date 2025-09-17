package com.cine.cinemovieservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "movie_genres",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"movie_id", "genre_id"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovieGenre extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;
}
