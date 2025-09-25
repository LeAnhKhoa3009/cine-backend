package com.cine.cinemovieservice.validator;


import com.cine.cinemovieservice.entity.Movie;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MovieValidator {

    public static void validate(Movie movie) {
        if (movie == null) {
            throw new IllegalArgumentException("Movie must not be null");
        }

        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            log.error("Movie title should not be null or empty");
            throw new IllegalArgumentException("Movie title should not be null or empty");
        }

        if (movie.getDuration() <= 0) {
            log.error("Movie duration should be greater than zero");
            throw new IllegalArgumentException("Movie duration should be greater than zero");
        }

        if (movie.getPremiereDate() == null) {
            log.error("Movie premiere date should not be null");
            throw new IllegalArgumentException("Movie premiere date should not be null");
        }
    }
}
