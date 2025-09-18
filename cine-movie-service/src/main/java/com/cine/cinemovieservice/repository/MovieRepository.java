package com.cine.cinemovieservice.repository;

import com.cine.cinemovieservice.entity.Movie;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends BaseRepository<Movie, Long> {
}
