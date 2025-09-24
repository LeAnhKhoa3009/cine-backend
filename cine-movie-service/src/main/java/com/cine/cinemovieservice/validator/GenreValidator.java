package com.cine.cinemovieservice.validator;

import com.cine.cinemovieservice.entity.Genre;
import com.cine.cinemovieservice.repository.GenresRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreValidator {

    private final GenresRepository genresRepository;

    public void validate(Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre must not be null");
        }

        if (genre.getName() == null || genre.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Genre name must not be null or empty");
        }

        genresRepository.findByName(genre.getName().trim())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(genre.getId())) {
                        throw new IllegalArgumentException(
                                "Genre name '" + genre.getName() + "' already exists"
                        );
                    }
                });
    }
}
