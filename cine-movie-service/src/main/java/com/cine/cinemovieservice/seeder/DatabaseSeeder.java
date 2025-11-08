package com.cine.cinemovieservice.seeder;

import com.cine.cinemovieservice.dto.ImportGenreDTO;
import com.cine.cinemovieservice.dto.ImportMovieDTO;
import com.cine.cinemovieservice.entity.Genre;
import com.cine.cinemovieservice.entity.ImageFolder;
import com.cine.cinemovieservice.entity.Movie;
import com.cine.cinemovieservice.repository.GenresRepository;
import com.cine.cinemovieservice.repository.ImageFolderRepository;
import com.cine.cinemovieservice.repository.MovieRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final MovieRepository movieRepository;
    private final GenresRepository genresRepository;
    private final ImageFolderRepository imageFolderRepository;
    private final ObjectMapper objectMapper;

    public DatabaseSeeder(MovieRepository movieRepository, GenresRepository genresRepository, ImageFolderRepository imageFolderRepository) {
        this.movieRepository = movieRepository;
        this.genresRepository = genresRepository;
        this.imageFolderRepository = imageFolderRepository;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Override
    public void run(String... args) throws Exception {
        //Read a json file init-movie-data.json in database package in resources folder as List<ImportGenreDTO>
        for (ImportGenreDTO genreDTO : loadGenres()) {
            if(genresRepository.findById(genreDTO.getId()).isEmpty()){
                Genre defaultGenre = Genre.builder()
                        .name(genreDTO.getName())
                        .icon(genreDTO.getIcon())
                        .deleted(false)
                        .build();
                genresRepository.save(defaultGenre);
            }
        }

        //Read a json file init-movie-data.json in database package in resources folder as List<ImportMovieDTO>
        for (ImportMovieDTO movieDTO : loadMovies()) {
            if(movieRepository.findById(movieDTO.getId()).isEmpty()){
                Set<Genre> genres = new HashSet<>(genresRepository.findAllById(movieDTO.getGenres()));
                Movie defaultMovie = Movie.builder()
                        .title(movieDTO.getTitle())
                        .description(movieDTO.getDescription())
                        .poster(movieDTO.getPoster())
                        .rating(movieDTO.getRating())
                        .premiereDate(movieDTO.getPremiereDate())
                        .duration(movieDTO.getDuration())
                        .deleted(false)
                        .genres(genres)
                        .build();

                movieRepository.save(defaultMovie);
            }
        }

        //Init root folder
        if(imageFolderRepository.findByName("root").isEmpty()){
            imageFolderRepository.save(ImageFolder.builder()
                    .name("root")
                    .build());
        }

        if(imageFolderRepository.findByName("test").isEmpty()){
            imageFolderRepository.save(ImageFolder.builder()
                    .name("test")
                    .build());
        }
    }

    private List<ImportGenreDTO> loadGenres() {
        try (InputStream inputStream = new ClassPathResource("database/init-genre-data.json").getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<List<ImportGenreDTO>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load genre data", e);
        }
    }

    private List<ImportMovieDTO> loadMovies() {
        try (InputStream inputStream = new ClassPathResource("database/init-movie-data.json").getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<List<ImportMovieDTO>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load movie data", e);
        }
    }
}
