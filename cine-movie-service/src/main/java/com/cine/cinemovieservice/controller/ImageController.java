package com.cine.cinemovieservice.controller;

import com.cine.cinemovieservice.dto.ApiResponse;
import com.cine.cinemovieservice.dto.DeleteImageResponseDTO;
import com.cine.cinemovieservice.dto.RawImageResponseDTO;
import com.cine.cinemovieservice.dto.UploadImageReponseDTO;
import com.cine.cinemovieservice.exception.NotModifiedException;
import com.cine.cinemovieservice.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneOffset;

@Slf4j
@RestController
@RequestMapping("api/v1/images")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "Upload Image")
    public ResponseEntity<ApiResponse<UploadImageReponseDTO>> upload(@RequestPart("file") MultipartFile file, @RequestParam(required = false) String fileName, @RequestParam(required = false) String folder) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<UploadImageReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(imageService.upload(file, fileName, folder))
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<UploadImageReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UploadImageReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Tag(name = "Serve Image")
    public ResponseEntity<byte[]> serve(@PathVariable Long id, @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch) {
        try {
            RawImageResponseDTO rawImageResponseDTO = imageService.get(id, ifNoneMatch);
            if(rawImageResponseDTO == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok()
                    .eTag(rawImageResponseDTO.eTag())
                    .contentType(MediaType.parseMediaType(rawImageResponseDTO.contentType()))
                    .lastModified(rawImageResponseDTO.updatedTime().toInstant(ZoneOffset.UTC))
                    .cacheControl(CacheControl.maxAge(java.time.Duration.ofSeconds(20)).cachePublic().immutable())
                    .body(rawImageResponseDTO.content());
        } catch (NotModifiedException e) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(e.getEtag()).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/{id}")
    @Tag(name = "Delete Image by ID")
    public ResponseEntity<ApiResponse<DeleteImageResponseDTO>> delete(@PathVariable Long id) {
        try {
            DeleteImageResponseDTO deleteImageResponseDTO = imageService.delete(id);
            if(deleteImageResponseDTO == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<DeleteImageResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Image not found with id " + id)
                                .build());
            }

            return ResponseEntity.ok(
                    ApiResponse.<DeleteImageResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(deleteImageResponseDTO)
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<DeleteImageResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<DeleteImageResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }
}
