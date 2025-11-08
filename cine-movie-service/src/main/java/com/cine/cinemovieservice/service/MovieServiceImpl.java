package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateMovieRequestDTO;
import com.cine.cinemovieservice.dto.MovieResponseDTO;
import com.cine.cinemovieservice.dto.UpdateMovieRequestDTO;
import com.cine.cinemovieservice.entity.Genre;
import com.cine.cinemovieservice.entity.Movie;
import com.cine.cinemovieservice.repository.GenresRepository;
import com.cine.cinemovieservice.repository.MovieRepository;
import com.cine.cinemovieservice.validator.MovieValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;
    private final GenreService genreService;
    private final GenresRepository genresRepository;
    private final MovieValidator movieValidator;

    public MovieServiceImpl(MovieRepository movieRepository, GenreService genreService, GenresRepository genresRepository, MovieValidator movieValidator) {
        this.movieRepository = movieRepository;
        this.genreService = genreService;
        this.genresRepository = genresRepository;
        this.movieValidator = movieValidator;
    }


    @Override
    public Page<MovieResponseDTO> getAllMovies(Pageable pageable) {
        try {
            log.info("Retrieving all movies");

            Page<Movie> moviePage = movieRepository.findAll(pageable);

            return moviePage.map(this::mapToDto);
        } catch (Exception e) {
            log.error("Error fetching all movies: {}", e.getMessage());
            return Page.empty();
        }
    }

    @Override
    public Optional<MovieResponseDTO> getDetails(Long id) {
        try {
            return movieRepository.findById(id)
                    .map(this::mapToDto);
        } catch (Exception e) {
            log.error("An error occurred while retrieving movie details with id {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
    @Override
    public Movie save(CreateMovieRequestDTO createMovieRequestDTO) {
        Movie movie = createMovieFromDto(createMovieRequestDTO);

        movieValidator.validate(movie);

        return movieRepository.save(movie);
    }

    @Override
    public Movie update(UpdateMovieRequestDTO updateMovieRequestDTO) {
        Optional<Movie> optionalMovie = movieRepository.findById(updateMovieRequestDTO.getId());
        if (optionalMovie.isPresent()) {
            Movie movie = optionalMovie.get();
            updateMovieFromDto(movie, updateMovieRequestDTO);

            movieValidator.validate(movie);

            return movieRepository.save(movie);
        }
        log.error("Movie not found with id {}", updateMovieRequestDTO.getId());
        return null;
    }



    @Override
    public void delete(Long id) {
        try {
            Optional<Movie> optionalMovie = movieRepository.findById(id);

            if (optionalMovie.isEmpty()) {
                log.error("Movie not found with id {}", id);
                return;
            }

            Movie movie = optionalMovie.get();
            movie.setDeleted(true);
            movieRepository.save(movie);
        } catch (Exception e) {
            log.error("Error soft deleting movie with id {}: {}", id, e.getMessage());
        }
    }

    @Override
    public Optional<MovieResponseDTO> restore(Long id) {
        try {
            Optional<Movie> optionalMovie = movieRepository.findById(id);

            if (optionalMovie.isEmpty()) {
                log.error("Movie not found with id {}", id);
                return Optional.empty();
            }

            Movie movie = optionalMovie.get();
            movie.setDeleted(false);
            Movie restoredMovie = movieRepository.save(movie);
            return Optional.of(mapToDto(restoredMovie));
        } catch (Exception e) {
            log.error("Error restoring movie with id {}: {}", id, e.getMessage());
            return Optional.empty();

        }
      }

    private Movie createMovieFromDto(CreateMovieRequestDTO createMovieRequestDTO) {

        Set<Genre> genres = new HashSet<>(genreService.fetchByIds(createMovieRequestDTO.getGenres()));

        return Movie.builder()
                .title(createMovieRequestDTO.getTitle())
                .poster(createMovieRequestDTO.getPoster())
                .description(createMovieRequestDTO.getDescription())
                .duration(createMovieRequestDTO.getDuration())
                .rating(createMovieRequestDTO.getRating())
                .premiereDate(createMovieRequestDTO.getPremiereDate())
                .genres(genres)
                .build();
    }

    private void updateMovieFromDto(Movie targetMovie, UpdateMovieRequestDTO movieDto) {
        targetMovie.setTitle(movieDto.getTitle());
        targetMovie.setPoster(movieDto.getPoster());
        targetMovie.setDescription(movieDto.getDescription());
        targetMovie.setDuration(movieDto.getDuration());
        targetMovie.setRating(movieDto.getRating());
        targetMovie.setPremiereDate(movieDto.getPremiereDate());
        targetMovie.setGenres(movieDto.getGenres().stream().map(genresRepository::findById).flatMap(Optional::stream).collect(Collectors.toSet())
        );
    }
    private MovieResponseDTO mapToDto(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .poster(movie.getPoster())
                .rating(movie.getRating())
                .premiereDate(movie.getPremiereDate())
                .duration(movie.getDuration())
                .genres(movie.getGenres())
                .deleted(movie.getDeleted())
                .build();
    }
}
