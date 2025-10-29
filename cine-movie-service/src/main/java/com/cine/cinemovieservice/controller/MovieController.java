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
@Tag(name = "Movies")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MovieResponseDTO>>> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pagination = Pageable.ofSize(size).withPage(page);
            Page<MovieResponseDTO> movies = movieService.getAllMovies(pagination);

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

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> getMovieById(@PathVariable @NotNull Long movieId) {
        try {
            return movieService.getDetails(movieId)
                    .map(movie -> ResponseEntity
                            .ok(ApiResponse.<MovieResponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                    .data(movie)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<MovieResponseDTO>builder()
                                    .status(ApiResponse.ApiResponseStatus.FAILURE)
                                    .message("Movie not found with id " + movieId)
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
    public ResponseEntity<ApiResponse<Movie>> createMovie(
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

    @DeleteMapping("/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> deleteMovie(@PathVariable @NotNull Long movieId) {
        try {
            Optional<MovieResponseDTO> existingMovie = movieService.getDetails(movieId);
            if (existingMovie.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<MovieResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Movie not found with id " + movieId)
                                .build());
            }
            movieService.delete(movieId);
            Optional<MovieResponseDTO> deletedMovie = movieService.getDetails(movieId);
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


    @PutMapping("/{movieId}")
    public ResponseEntity<ApiResponse<Movie>> updateMovie(
            @PathVariable Long movieId,
            @RequestBody @Valid UpdateMovieRequestDTO updateMovieRequestDTO) {
        try {
            updateMovieRequestDTO.setId(movieId);

            Movie updatedMovie = movieService.update(updateMovieRequestDTO);

            if (updatedMovie == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Movie>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Movie not found with id " + movieId)
                                .build());
            }
            movieService.delete(movieId);
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

    @PutMapping("/restore/{movieId}")
    public ResponseEntity<ApiResponse<MovieResponseDTO>> restoreMovie(@PathVariable @NotNull Long movieId) {
        try {
            Optional<MovieResponseDTO> restoredMovie = movieService.restore(movieId);
            if (restoredMovie.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<MovieResponseDTO>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Movie not found or not deleted with id " + movieId)
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






