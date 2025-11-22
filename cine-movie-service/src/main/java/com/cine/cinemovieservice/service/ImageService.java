package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.DeleteImageResponseDTO;
import com.cine.cinemovieservice.dto.RawImageResponseDTO;
import com.cine.cinemovieservice.dto.UploadImageReponseDTO;
import com.cine.cinemovieservice.entity.ImageFolder;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    UploadImageReponseDTO upload(MultipartFile file, String fileName, String folderName);

    RawImageResponseDTO get(Long id, String ifNoneMatch);

    DeleteImageResponseDTO delete(Long id);

    ImageFolder getOrDefaultRootFolder(String folderName);
}
