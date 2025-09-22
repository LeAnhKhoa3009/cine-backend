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
@Tag(name = "Genres")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Genre>>> getAllGenres() {
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

    @GetMapping("/{genreId}")
    public ResponseEntity<ApiResponse<Genre>> getGenreById(@PathVariable @NotNull Long genreId) {
        try {
            return genreService.getDetails(genreId)
                    .map(genre -> ResponseEntity.ok(
                            ApiResponse.<Genre>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(genre)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<Genre>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("Genre not found with id " + genreId)
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
    public ResponseEntity<ApiResponse<Genre>> createGenre(
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

    @PutMapping("/{genreId}")
    public ResponseEntity<ApiResponse<Genre>> updateGenre(
            @PathVariable Long genreId,
            @RequestBody @Valid UpdateGenreRequestDTO updateGenreRequestDTO) {
        try {
            updateGenreRequestDTO.setId(genreId);
            Genre updatedGenre = genreService.update(updateGenreRequestDTO);

            if (updatedGenre == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Genre>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Genre not found with id " + genreId)
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

    @DeleteMapping("/{genreId}")
    public ResponseEntity<ApiResponse<Genre>> deleteGenre(@PathVariable @NotNull Long genreId) {
        try {
            Optional<Genre> isActive = genreService.getDetails(genreId);
            genreService.delete(genreId);

            if (isActive.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Genre>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Genre not found with id " + genreId)
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
