package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.DeleteImageFolderResponseDTO;
import com.cine.cinemovieservice.dto.RetrieveImageFolderDTO;
import org.springframework.data.domain.Page;

public interface ImageFolderService {
    Page<RetrieveImageFolderDTO> fetchAll(int page, int size);

    RetrieveImageFolderDTO create(String folderName);

    DeleteImageFolderResponseDTO delete(Long id, boolean deleteItem);

    RetrieveImageFolderDTO update(Long id, String newFolderName);
}
