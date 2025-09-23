package com.cine.cinemovieservice.validator;

import com.cine.cinemovieservice.entity.Movie;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MovieValidator {

    public static boolean isValid(Movie movie) {
        if (movie == null) {
            return false;
        }

        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            log.error("Movie title should not be null or empty");
            return false;
        }

        if (movie.getDuration() <= 0) {
            log.error("Movie duration should be greater than zero");
            return false;
        }

        if (movie.getPremiereDate() == null) {
            log.error("Movie premiere date should not be null");
            return false;
        }
        return true;
    }
}
