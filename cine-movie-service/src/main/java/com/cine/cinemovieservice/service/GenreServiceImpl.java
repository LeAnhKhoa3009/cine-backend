package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateGenreRequestDTO;
import com.cine.cinemovieservice.dto.UpdateGenreRequestDTO;
import com.cine.cinemovieservice.entity.Genre;
import com.cine.cinemovieservice.repository.GenresRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@Slf4j
public class GenreServiceImpl implements GenreService{
    @Autowired
    private GenresRepository genresRepository;

    @Override
    public List<Genre> getAllGenre() {
        try {
            log.info("Getting all genres");
            return genresRepository.findAll();

        }catch (Exception e){
            log.error(e.getMessage());
            return List.of();
        }
    }

    @Override
    public Optional<Genre> getDetails(Long id) {
        try {
            log.info("Getting details of genre with id {}", id);
            return genresRepository.findById(id);
        }catch (Exception e){
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Genre save(CreateGenreRequestDTO createGenreRequestDTO) {
        Genre genre = createGenreFromDTO(createGenreRequestDTO);
        return genresRepository.save(genre);
    }

    @Override
    public Genre update(UpdateGenreRequestDTO updateGenreRequestDTO) {
        try {
            Optional<Genre> optionalGenre = genresRepository.findById(updateGenreRequestDTO.getId());
            if (optionalGenre.isPresent()) {
                Genre genre = optionalGenre.get();
                updateGenreFromDTO(genre, updateGenreRequestDTO);
                return genresRepository.save(genre);
            }
            log.error("Genre not found with id {}", updateGenreRequestDTO.getId());
            return null;
        }catch(Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Optional<Genre> optionalGenre = genresRepository.findById(id);

            if (optionalGenre.isEmpty()) {
                log.error("Movie not found with id {}", id);
                return;
            }

            Genre genre = optionalGenre.get();
            genre.setDeleted(true);
            genresRepository.save(genre);

            log.info("Soft deleted genre with id {}", id);
        } catch (Exception e) {
            log.error("Error soft deleting genre with id {}: {}", id, e.getMessage());
        }
    }

    private Genre createGenreFromDTO(CreateGenreRequestDTO createGenreRequestDTO) {
        return Genre.builder()
                .name(createGenreRequestDTO.getName())
                .icon(createGenreRequestDTO.getIcon())
                .build();
    }
    private void updateGenreFromDTO(Genre targetGenre, UpdateGenreRequestDTO updateGenreRequestDTO) {
        targetGenre.setName(updateGenreRequestDTO.getName());
        targetGenre.setIcon(updateGenreRequestDTO.getIcon());
    }
}
