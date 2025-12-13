package com.cine.cinemovieservice.controller;

import com.cine.cinemovieservice.dto.ApiResponse;
import com.cine.cinemovieservice.dto.CreateMovieRequestDTO;
import com.cine.cinemovieservice.dto.MovieResponseDTO;
import com.cine.cinemovieservice.dto.UpdateMovieRequestDTO;
import com.cine.cinemovieservice.entity.Movie;
import com.cine.cinemovieservice.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Optional;

@RestController
@RequestMapping(value = "api/v1/movies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    @Tag(name = "Fetch Movies")
    public ResponseEntity<ApiResponse<Page<MovieResponseDTO>>> fetchAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long genreId) {
        try {
            Pageable pagination = Pageable.ofSize(size).withPage(page);
            Page<MovieResponseDTO> movies = movieService.fetchAll(pagination, genreId);

            return ResponseEntity.ok(
                    ApiResponse.<Page<MovieResponseDTO>>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(movies)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.<Page<MovieResponseDTO>>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Tag(name = "Fetch Movie by ID")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> fetchById(@PathVariable @NotNull Long id) {
        try {
            return movieService.fetchById(id)
                    .map(movie -> ResponseEntity
                            .ok(ApiResponse.<MovieResponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(movie)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<MovieResponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("Movie not found with id " + id)
                                    .build()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MovieResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }


    @PostMapping
    @Tag(name = "Create Movie")
    public ResponseEntity<ApiResponse<Movie>> create(
            @Valid @RequestBody CreateMovieRequestDTO request) {
        try {
            Movie savedMovie = movieService.save(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.<Movie>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(savedMovie)
                            .build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Movie>builder()
                            .status(ApiResponse.ApiResponseStatus.FAILURE)
                            .message(e.getMessage())
                            .build());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Movie>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @Tag(name = "Delete Movie")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> delete(@PathVariable @NotNull Long id) {
        try {
            Optional<MovieResponseDTO> existingMovie = movieService.fetchById(id);
            if (existingMovie.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<MovieResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Movie not found with id " + id)
                                .build());
            }
            movieService.delete(id);
            Optional<MovieResponseDTO> deletedMovie = movieService.fetchById(id);
            return ResponseEntity
                    .ok(ApiResponse.<MovieResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .message("Movie deleted successfully")
                            .data(deletedMovie.orElse(existingMovie.get()))
                            .build());

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MovieResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }


    @PutMapping("/{id}")
    @Tag(name = "Update Movie")
    public ResponseEntity<ApiResponse<Movie>> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMovieRequestDTO updateMovieRequestDTO) {
        try {
            updateMovieRequestDTO.setId(id);

            Movie updatedMovie = movieService.update(updateMovieRequestDTO);

            if (updatedMovie == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Movie>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Movie not found with id " + id)
                                .build());
            }
            movieService.delete(id);
            return ResponseEntity
                    .ok(ApiResponse.<Movie>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(updatedMovie)
                            .message("Movie updated successfully")
                            .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Movie>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @PutMapping("/{id}/restore")
    @Tag(name = "Restore Movie")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> restore(@PathVariable @NotNull Long id) {
        try {
            Optional<MovieResponseDTO> restoredMovie = movieService.restore(id);
            if (restoredMovie.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<MovieResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Movie not found or not deleted with id " + id)
                                .build());
            }
            return ResponseEntity
                    .ok(ApiResponse.<MovieResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(restoredMovie.get())
                            .message("Movie restored successfully")
                            .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<MovieResponseDTO>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

}






