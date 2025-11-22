package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateGenreRequestDTO;
import com.cine.cinemovieservice.dto.UpdateGenreRequestDTO;
import com.cine.cinemovieservice.entity.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreService {

    List<Genre> fetchAll();

    Optional<Genre> fetchById(Long id);

    Genre save(CreateGenreRequestDTO createGenreRequestDTO);

    Genre update(UpdateGenreRequestDTO updateGenreRequestDTO);

    void delete(Long id);

    List<Genre> fetchByIds(Set<Long> ids);
}
