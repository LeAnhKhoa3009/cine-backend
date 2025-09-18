package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateGenreRequestDTO;
import com.cine.cinemovieservice.dto.CreateMovieRequestDTO;
import com.cine.cinemovieservice.dto.UpdateGenreRequestDTO;
import com.cine.cinemovieservice.dto.UpdateMovieRequestDTO;
import com.cine.cinemovieservice.entity.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreService {

    List<Genre> getAllGenre();

    Optional<Genre> getDetails(Long id);

    Genre save(CreateGenreRequestDTO createGenreRequestDTO);

    Genre update(UpdateGenreRequestDTO updateGenreRequestDTO);

    void delete(Long id);
}
