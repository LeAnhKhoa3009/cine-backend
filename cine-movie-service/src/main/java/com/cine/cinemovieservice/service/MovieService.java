package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateMovieRequestDTO;
import com.cine.cinemovieservice.dto.MovieResponseDTO;
import com.cine.cinemovieservice.dto.UpdateMovieRequestDTO;
import com.cine.cinemovieservice.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MovieService {

    Page<MovieResponseDTO> fetchAll(Pageable pageable, Long genreId);

    Optional<MovieResponseDTO> fetchById(Long id);

    Movie save(CreateMovieRequestDTO createMovieRequestDTO);

    Movie update(UpdateMovieRequestDTO updateMovieRequestDTO);

    void delete(Long id);

    Optional<MovieResponseDTO> restore(Long id);
}
