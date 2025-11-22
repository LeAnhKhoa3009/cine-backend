package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.DeleteImageResponseDTO;
import com.cine.cinemovieservice.dto.RawImageResponseDTO;
import com.cine.cinemovieservice.dto.UploadImageReponseDTO;
import com.cine.cinemovieservice.entity.Image;
import com.cine.cinemovieservice.entity.ImageFolder;
import com.cine.cinemovieservice.exception.NotModifiedException;
import com.cine.cinemovieservice.repository.ImageFolderRepository;
import com.cine.cinemovieservice.repository.ImageRepository;
import com.cine.cinemovieservice.validator.ImageValidator;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageValidator imageValidator;
    public static final String IMAGE_URL_SRC = "/api/v1/images/%d/raw";
    private final MovieService movieService;
    private final ImageFolderRepository imageFolderRepository;

    @Value("${cine.folder.item.max.size}")
    private Long maxFolderItem;

    public ImageServiceImpl(ImageRepository imageRepository, ImageValidator imageValidator, MovieService movieService, ImageFolderRepository imageFolderRepository) {
        this.imageRepository = imageRepository;
        this.imageValidator = imageValidator;
        this.movieService = movieService;
        this.imageFolderRepository = imageFolderRepository;
    }

    @Override
    @Transactional
    public UploadImageReponseDTO upload(MultipartFile file, String fileName, String folderName) {
        imageValidator.validate(file);

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("read error", e);
        }

        String checksum = sha256Hex(bytes);

        String imageName = file.getOriginalFilename();
        if (StringUtils.isNotBlank(fileName)) {
            String[] arrStr = file.getOriginalFilename().split("\\.");
            String extension = "." + arrStr[arrStr.length - 1];
            imageName = fileName + extension;
        }


        if (StringUtils.isBlank(folderName)) {
            folderName = "root";
        }

        ImageFolder folder = getOrDefaultRootFolder(folderName);

        //Validate file
        if(!folder.getName().equals("root") && folder.getImages().size() >= maxFolderItem){
            throw new RuntimeException("Folder " + folderName + " has reached the maximum number of items: " + maxFolderItem);
        }

        Image newImage = imageRepository.save(Image.builder()
                .name(imageName)
                .size(bytes.length)
                .checksumSha256(checksum)
                .contentType(file.getContentType())
                .content(bytes)
                .folder(folder)
                .build());

        return new UploadImageReponseDTO(newImage.getId(), imageName, newImage.getChecksumSha256(), folder.getName());
    }

    @Override
    public RawImageResponseDTO get(Long id, String ifNoneMatch) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }

        Image img = imageRepository.findById(id).orElse(null);
        if (img == null) {
            return null;
        }

        String etag = (img.getChecksumSha256() != null ? img.getChecksumSha256() : String.valueOf(img.getId()));
        if (etag.equals(ifNoneMatch)) {
            throw new NotModifiedException("Image hasn't been changed", etag);//
        }

        return RawImageResponseDTO.builder()
                .eTag(etag)
                .updatedTime(img.getUpdatedAt())
                .contentType(img.getContentType())
                .content(img.getContent())
                .build();
    }

    @Override
    public DeleteImageResponseDTO delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }

        if (imageRepository.findById(id).isEmpty()) {
            return null;
        }
        imageRepository.deleteById(id);
        return new DeleteImageResponseDTO(id);
    }


    private String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public ImageFolder getOrDefaultRootFolder(String folderName){
        ImageFolder folder;
        Optional<ImageFolder> folderOpt = imageFolderRepository.findByName(folderName);
        if (folderOpt.isEmpty()) {
            Optional<ImageFolder> rootFolderOpt = imageFolderRepository.findByName("root");
            if (rootFolderOpt.isEmpty()) {
                folder = imageFolderRepository.save(ImageFolder.builder().name("root").images(List.of()).build());
            }else{
                folder = rootFolderOpt.get();
            }
        }else{
            folder = folderOpt.get();
        }
        return folder;
    }
}
