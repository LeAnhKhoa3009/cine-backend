package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.DeleteImageFolderResponseDTO;
import com.cine.cinemovieservice.dto.RetrieveImageDTO;
import com.cine.cinemovieservice.dto.RetrieveImageFolderDTO;
import com.cine.cinemovieservice.entity.Image;
import com.cine.cinemovieservice.entity.ImageFolder;
import com.cine.cinemovieservice.entity.Movie;
import com.cine.cinemovieservice.repository.ImageFolderRepository;
import com.cine.cinemovieservice.repository.ImageRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.cine.cinemovieservice.service.ImageServiceImpl.IMAGE_URL_SRC;

@Service
public class ImageFolderServiceImpl implements ImageFolderService {


    private final ImageFolderRepository imageFolderRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;

    public ImageFolderServiceImpl(ImageFolderRepository imageFolderRepository, ImageRepository imageRepository, ImageService imageService) {
        this.imageFolderRepository = imageFolderRepository;
        this.imageRepository = imageRepository;
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public Page<RetrieveImageFolderDTO> fetchAll(int page, int size) {
        Pageable pagination = Pageable.ofSize(size).withPage(page);
        Page<ImageFolder> folderPage = imageFolderRepository.findAll(pagination);
        return folderPage.map(this::buildImageFolderDTO);
    }

    @Override
    public RetrieveImageFolderDTO create(String folderName) {
        if(StringUtils.isBlank(folderName)){
            throw new RuntimeException("Folder name cannot be empty");
        }

        ImageFolder imageFolder = imageFolderRepository.save(ImageFolder.builder().name(folderName).build());
        return RetrieveImageFolderDTO.builder()
                .id(imageFolder.getId())
                .name(imageFolder.getName())
                .build();
    }

    @Override
    public RetrieveImageFolderDTO update(Long id, String newFolderName) {
        Optional<ImageFolder> imageFolderOpt = imageFolderRepository.findById(id);
        if(imageFolderOpt.isEmpty()){
            return null;
        }

        ImageFolder imageFolder = imageFolderOpt.get();

        if(imageFolder.getName().equals("root")){
            throw new RuntimeException("Cannot update root folder");
        }

        imageFolder.setName(newFolderName);
        ImageFolder updatedImageFolder = imageFolderRepository.save(imageFolder);
        return buildImageFolderDTO(updatedImageFolder);
    }

    @Override
    @Transactional
    public DeleteImageFolderResponseDTO delete(Long id, boolean deleteItem) {
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }

        Optional<ImageFolder> imageFolderOpt = imageFolderRepository.findById(id);
        if(imageFolderOpt.isEmpty()){
            return null;
        }

        ImageFolder imageFolder = imageFolderOpt.get();

        if(imageFolder.getName().equals("root")){
            throw new RuntimeException("Cannot delete root folder");
        }

        if(!imageFolder.getImages().isEmpty()){
            if(deleteItem){
                imageRepository.deleteAll(imageFolder.getImages());
            }else{
                //Attach iamge to root folder
                List<Image> relocatedImages = imageFolder.getImages().stream().map(image -> {
                    ImageFolder rootFolder = imageService.getOrDefaultRootFolder("root");
                    image.setFolder(rootFolder);
                    return image;
                }).toList();
                imageRepository.saveAll(relocatedImages);
            }
        }

        imageFolderRepository.deleteById(id);
        return DeleteImageFolderResponseDTO.builder()
                .id(id)
                .build();
    }



    private RetrieveImageFolderDTO buildImageFolderDTO(ImageFolder folder){
        return RetrieveImageFolderDTO.builder()
                .id(folder.getId())
                .name(folder.getName())
                .images(folder.getImages().stream().map(image -> RetrieveImageDTO.builder()
                                .url(IMAGE_URL_SRC.formatted(image.getId()))
                                .id(image.getId())
                                .name(image.getName())
                                .size(image.getSize())
                                .contentType(image.getContentType())
                                .folder(Optional.ofNullable(image.getMovie()).map(Movie::getTitle).orElse(folder.getName()))
                                .build())
                        .toList())
                .build();
    }
}
