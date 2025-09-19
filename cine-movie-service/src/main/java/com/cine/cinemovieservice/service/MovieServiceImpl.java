package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateMovieRequestDTO;
import com.cine.cinemovieservice.dto.UpdateMovieRequestDTO;
import com.cine.cinemovieservice.entity.Movie;
import com.cine.cinemovieservice.repository.MovieRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public List<Movie> getAllMovies() {
            try {
                return movieRepository.findAll();
            }catch (Exception e){
                log.error("An error occurred while retrieving all movies");
                return List.of();
            }
    }

    @Override
    public Optional<Movie> getDetails(Long id) {
        try {
            return movieRepository.findById(id);
        }catch (Exception e){
            log.error("An error occurred while retrieving movie details with id {}", id);
            return Optional.empty();
        }
    }

    @Override
    public Movie save(CreateMovieRequestDTO createMovieRequestDTO) {
            Movie movie = createMovieFromDto(createMovieRequestDTO);
            return movieRepository.save(movie);
    }

    @Override
    public Movie update(UpdateMovieRequestDTO updateMovieRequestDTO) {
        try {
            Optional<Movie> optionalMovie = movieRepository.findById(updateMovieRequestDTO.getId());
            if (optionalMovie.isPresent()) {
                Movie movie = optionalMovie.get();
                updateMovieFromDto(movie, updateMovieRequestDTO);
                return movieRepository.save(movie);
            }
            log.error("Movie not found with id {}", updateMovieRequestDTO.getId());
            return null;
        }catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
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

    private Movie createMovieFromDto(CreateMovieRequestDTO createMovieRequestDTO) {
        return Movie.builder()
                .title(createMovieRequestDTO.getTitle())
                .description(createMovieRequestDTO.getDescription())
                .duration(createMovieRequestDTO.getDuration())
                .premiereDate(createMovieRequestDTO.getPremiereDate())
                .build();
    }
    private void updateMovieFromDto(Movie targetMovie, UpdateMovieRequestDTO movieDto) {
        targetMovie.setTitle(movieDto.getTitle());
        targetMovie.setDescription(movieDto.getDescription());
        targetMovie.setDuration(movieDto.getDuration());
        targetMovie.setPremiereDate(movieDto.getPremiereDate());
    }
}
