package com.cine.cinemovieservice.repository;

import com.cine.cinemovieservice.entity.Genre;
import org.springframework.stereotype.Repository;

@Repository
public interface GenresRepository extends BaseRepository<Genre, Long> {
}
