package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.DeleteImageResponseDTO;
import com.cine.cinemovieservice.dto.RawImageResponseDTO;
import com.cine.cinemovieservice.dto.RetrieveImageDTO;
import com.cine.cinemovieservice.dto.UploadImageReponseDTO;
import com.cine.cinemovieservice.entity.Image;
import com.cine.cinemovieservice.exception.NotModifiedException;
import com.cine.cinemovieservice.repository.ImageRepository;
import com.cine.cinemovieservice.validator.ImageValidator;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.util.HexFormat;

@Service
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageValidator imageValidator;
    private static final String IMAGE_URL_SRC = "/api/v1/images/%d/raw";

    public ImageServiceImpl(ImageRepository imageRepository, ImageValidator imageValidator) {
        this.imageRepository = imageRepository;
        this.imageValidator = imageValidator;
    }

    @Override
    @Transactional
    public UploadImageReponseDTO upload(MultipartFile file, String name) {
        imageValidator.validate(file);

        byte[] bytes;
        try { bytes = file.getBytes(); } catch (Exception e) {
            throw new RuntimeException("read error", e);
        }

        String checksum = sha256Hex(bytes);

        String imageName = file.getOriginalFilename();
        if(StringUtils.isNotBlank(name)){
            String[] arrStr = file.getOriginalFilename().split("\\.");
            String extension = "." + arrStr[arrStr.length - 1];
            imageName = name + extension;
        }

        Image newImage = imageRepository.save(Image.builder()
                .name(imageName)
                .size(bytes.length)
                .checksumSha256(checksum)
                .contentType(file.getContentType())
                .content(bytes)
                .build());

        return new UploadImageReponseDTO(newImage.getId(), imageName, newImage.getChecksumSha256());
    }

    @Override
    public RawImageResponseDTO get(Long id, String ifNoneMatch) {
        if(id == null){
            throw new IllegalArgumentException("Id must not be null");
        }

        Image img = imageRepository.findById(id).orElse(null);
        if(img == null){
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
    public Page<RetrieveImageDTO> fetchAll(int page, int size) {
        Pageable pagination = Pageable.ofSize(size).withPage(page);
        Page<Image> pageOfImage = imageRepository.findAll(pagination);
        return pageOfImage.map(image -> RetrieveImageDTO.builder()
                .url(IMAGE_URL_SRC.formatted(image.getId()))
                .id(image.getId())
                .name(image.getName())
                .size(image.getSize())
                .contentType(image.getContentType())
                .build());
    }

    @Override
    public DeleteImageResponseDTO delete(Long id) {
        if(id == null){
            throw new IllegalArgumentException("Id must not be null");
        }

        if(imageRepository.findById(id).isEmpty()){
            return null;
        }
        imageRepository.deleteById(id);
        return new DeleteImageResponseDTO(id);
    }


    private String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(data));
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
