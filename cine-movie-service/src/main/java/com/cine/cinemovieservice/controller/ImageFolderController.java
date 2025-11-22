package com.cine.cinemovieservice.controller;

import com.cine.cinemovieservice.dto.*;
import com.cine.cinemovieservice.service.ImageFolderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/folders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ImageFolderController {
    private final ImageFolderService imageFolderService;

    public ImageFolderController(ImageFolderService imageFolderService) {
        this.imageFolderService = imageFolderService;
    }

    @GetMapping
    @Transactional
    @Tag(name = "Fetch Image Folders")
    public ResponseEntity<ApiResponse<Page<RetrieveImageFolderDTO>>> fetchAll(@RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size) {
        try {
            Page<RetrieveImageFolderDTO> folders = imageFolderService.fetchAll(page, size);
            return ResponseEntity.ok(
                    ApiResponse.<Page<RetrieveImageFolderDTO>>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(folders)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<Page<RetrieveImageFolderDTO>>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }


    @PostMapping
    @Tag(name = "Create Image Folder")
    public ResponseEntity<ApiResponse<RetrieveImageFolderDTO>> create(@RequestBody SaveImageFolderRequestDTO request) {
        try {
            RetrieveImageFolderDTO folderDTO = imageFolderService.create(request.name());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<RetrieveImageFolderDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(folderDTO)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<RetrieveImageFolderDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<RetrieveImageFolderDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}")
    @Tag(name = "Update Image Folder")
    public ResponseEntity<ApiResponse<RetrieveImageFolderDTO>> update(@PathVariable Long id, @RequestBody SaveImageFolderRequestDTO request) {
        try {
            RetrieveImageFolderDTO folderDTO = imageFolderService.update(id, request.name());
            return ResponseEntity.ok(ApiResponse.<RetrieveImageFolderDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(folderDTO)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<RetrieveImageFolderDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<RetrieveImageFolderDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @Tag(name = "Delete Image Folder")
    public ResponseEntity<ApiResponse<DeleteImageFolderResponseDTO>> delete(@PathVariable Long id, @RequestParam(defaultValue = "false", required = false) Boolean deleteItem) {
        try{
            DeleteImageFolderResponseDTO responseDTO = imageFolderService.delete(id, deleteItem);
            if(responseDTO == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<DeleteImageFolderResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Image folder not found with id: " + id)
                                .build());
            }
            return ResponseEntity.ok(
                    ApiResponse.<DeleteImageFolderResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(responseDTO)
                            .message("Image folder deleted successfully")
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<DeleteImageFolderResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<DeleteImageFolderResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }

    }
}
