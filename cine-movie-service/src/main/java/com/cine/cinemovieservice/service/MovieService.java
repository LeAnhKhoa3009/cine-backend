package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateMovieRequestDTO;
import com.cine.cinemovieservice.dto.UpdateMovieRequestDTO;
import com.cine.cinemovieservice.entity.Movie;

import java.util.List;
import java.util.Optional;

public interface MovieService {

    List<Movie> getAllMovies();

    Optional<Movie> getDetails(Long id);

    Movie save(CreateMovieRequestDTO createMovieRequestDTO);

    Movie update(UpdateMovieRequestDTO updateMovieRequestDTO);

    void delete(Long id);
}
