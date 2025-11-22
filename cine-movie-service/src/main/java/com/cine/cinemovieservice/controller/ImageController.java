package com.cine.cinemovieservice.controller;

import com.cine.cinemovieservice.dto.*;
import com.cine.cinemovieservice.exception.NotModifiedException;
import com.cine.cinemovieservice.service.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneOffset;

@RestController
@RequestMapping("api/v1/images")
@Tag(name = "Images")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    @Tag(name = "Upload an image")
    public ResponseEntity<ApiResponse<UploadImageReponseDTO>> upload(@RequestPart("file") MultipartFile file, @RequestParam(required = false) String name) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<UploadImageReponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(imageService.upload(file, name))
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
    @Tag(name = "Serve image by id with caching")
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
                    .cacheControl(CacheControl.maxAge(java.time.Duration.ofDays(30)).cachePublic().immutable())
                    .body(rawImageResponseDTO.content());
        } catch (NotModifiedException e) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(e.getEtag()).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Tag(name = "Fetch all image summaries with pagination")
    public ResponseEntity<ApiResponse<Page<RetrieveImageDTO>>> fetchAll(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        try {
            Page<RetrieveImageDTO> users = imageService.fetchAll(page, size);
            return ResponseEntity.ok(
                    ApiResponse.<Page<RetrieveImageDTO>>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(users)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<Page<RetrieveImageDTO>>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @Tag(name = "Delete image")
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
