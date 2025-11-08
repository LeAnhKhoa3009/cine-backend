package com.cine.cinemovieservice.repository;

import com.cine.cinemovieservice.entity.ImageFolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageFolderRepository extends BaseRepository<ImageFolder, Long>{
    Optional<ImageFolder> findByName(String folderName);
}
