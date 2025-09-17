package com.cine.cinemovieservice.repository;

import com.cine.cinemovieservice.entity.Session;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends BaseRepository<Session, Long> {
}
