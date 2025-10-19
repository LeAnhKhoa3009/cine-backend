package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.DeleteImageResponseDTO;
import com.cine.cinemovieservice.dto.RawImageResponseDTO;
import com.cine.cinemovieservice.dto.RetrieveImageDTO;
import com.cine.cinemovieservice.dto.UploadImageReponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    UploadImageReponseDTO upload(MultipartFile file, String name);

    RawImageResponseDTO get(Long id, String ifNoneMatch);

    Page<RetrieveImageDTO> fetchAll(int page, int size);

    DeleteImageResponseDTO delete(Long id);
}
