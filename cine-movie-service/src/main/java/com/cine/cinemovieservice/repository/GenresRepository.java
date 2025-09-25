package com.cine.cinemovieservice.repository;

import com.cine.cinemovieservice.entity.Genre;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenresRepository extends BaseRepository<Genre, Long> {
    Optional<Genre> findByName(String name);}
