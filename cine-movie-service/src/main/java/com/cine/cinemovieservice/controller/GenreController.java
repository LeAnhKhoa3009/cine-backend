package com.cine.cinemovieservice.controller;

import com.cine.cinemovieservice.dto.ApiResponse;
import com.cine.cinemovieservice.dto.CreateGenreRequestDTO;
import com.cine.cinemovieservice.dto.UpdateGenreRequestDTO;
import com.cine.cinemovieservice.entity.Genre;
import com.cine.cinemovieservice.service.GenreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/v1/genres")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping
    @Tag(name = "Fetch All Genres")
    public ResponseEntity<ApiResponse<List<Genre>>> fetchAll() {
        try {
            return ResponseEntity.ok(
                    ApiResponse.<List<Genre>>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(genreService.fetchAll())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Genre>>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Tag(name = "Fetch Genre by ID")
    public ResponseEntity<ApiResponse<Genre>> fetchById(@PathVariable @NotNull Long id) {
        try {
            return genreService.fetchById(id)
                    .map(genre -> ResponseEntity.ok(
                            ApiResponse.<Genre>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(genre)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<Genre>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("Genre not found with id " + id)
                                    .build()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Genre>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @PostMapping
    @Tag(name = "Create Genre")
    public ResponseEntity<ApiResponse<Genre>> create(
            @Valid @RequestBody CreateGenreRequestDTO request) {
        try {
            Genre savedGenre = genreService.save(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<Genre>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(savedGenre)
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Genre>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Genre>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @PutMapping("/{id}")
    @Tag(name = "Update Genre")
    public ResponseEntity<ApiResponse<Genre>> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateGenreRequestDTO updateGenreRequestDTO) {
        try {
            updateGenreRequestDTO.setId(id);
            Genre updatedGenre = genreService.update(updateGenreRequestDTO);

            if (updatedGenre == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Genre>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Genre not found with id " + id)
                                .build());
            }

            return ResponseEntity.ok(
                    ApiResponse.<Genre>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(updatedGenre)
                            .message("Genre updated successfully")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Genre>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @Tag(name = "Delete Genre by ID")
    public ResponseEntity<ApiResponse<Genre>> delete(@PathVariable @NotNull Long id) {
        try {
            Optional<Genre> isActive = genreService.fetchById(id);
            genreService.delete(id);

            if (isActive.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Genre>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Genre not found with id " + id)
                                .build());
            }

            return ResponseEntity.ok(
                    ApiResponse.<Genre>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .message("Genre deleted successfully")
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Genre>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }
}
