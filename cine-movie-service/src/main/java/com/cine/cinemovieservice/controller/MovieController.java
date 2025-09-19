package com.cine.cinemovieservice.controller;

import com.cine.cinemovieservice.dto.ApiResponse;
import com.cine.cinemovieservice.dto.CreateMovieRequestDTO;
import com.cine.cinemovieservice.dto.UpdateMovieRequestDTO;
import com.cine.cinemovieservice.entity.Movie;
import com.cine.cinemovieservice.service.MovieService;
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
@RequestMapping(value = "api/v1/movies")
@Tag(name = "Movies")
@CrossOrigin(origins = "*", maxAge = 3600)

public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Movie>>> getAllMovies() {
        try {
            return ResponseEntity
                    .ok(ApiResponse.<List<Movie>>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .data(movieService.getAllMovies())
                            .build());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Movie>>builder()
                            .status(ApiResponse.ApiResponseStatus.ERROR)
                            .message("Internal error. Please contact administrator.")
                            .build());
        }
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<ApiResponse<Movie>> getMovieById(@PathVariable @NotNull Long movieId) {
            try {
                return movieService.getDetails(movieId)
                        .map(movie -> ResponseEntity
                                .ok(ApiResponse.<Movie>builder()
                                        .status(ApiResponse.ApiResponseStatus.SUCCESS)
                                        .data(movie)
                                        .build()))
                        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.<Movie>builder()
                                        .status(ApiResponse.ApiResponseStatus.FAILURE)
                                        .message("Movie not found with id " + movieId)
                                        .build()));
            } catch (Exception e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.<Movie>builder()
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
    public ResponseEntity<ApiResponse<Movie>> deleteMovie(@PathVariable @NotNull Long movieId) {
        try {
            Optional<Movie> isActive = movieService.getDetails(movieId);
            movieService.delete(movieId);

            if (isActive.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Movie>builder()
                                .status(ApiResponse.ApiResponseStatus.FAILURE)
                                .message("Movie not found with id " + movieId)
                                .build());
            }

            return ResponseEntity
                    .ok(ApiResponse.<Movie>builder()
                            .status(ApiResponse.ApiResponseStatus.SUCCESS)
                            .message("Movie deleted successfully")
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

}






