package com.cine.cinemovieservice.repository;

import com.cine.cinemovieservice.entity.Image;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository  extends BaseRepository<Image, Long> {
}
