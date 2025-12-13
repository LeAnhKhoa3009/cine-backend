package com.cine.cinemovieservice.specification;

import com.cine.cinemovieservice.entity.Genre;
import com.cine.cinemovieservice.entity.Movie;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;

public class MovieSpecification {

    public static Specification<Movie> hasGenreId(Long genreId) {
        return (root, query, cb) -> {
            if (genreId == null) {
                return cb.conjunction();
            }

            Join<Movie, Genre> genresJoin = root.join("genres");
            return cb.equal(genresJoin.get("id"), genreId);
        };
    }
}
