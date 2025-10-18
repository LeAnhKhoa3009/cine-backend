package com.cine.cinemovieservice.service;

import com.cine.cinemovieservice.dto.CreateGenreRequestDTO;
import com.cine.cinemovieservice.dto.UpdateGenreRequestDTO;
import com.cine.cinemovieservice.entity.Genre;
import com.cine.cinemovieservice.repository.GenresRepository;
import com.cine.cinemovieservice.validator.GenreValidator;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class GenreServiceImpl implements GenreService{

    private final GenresRepository genresRepository;
    private final GenreValidator genreValidator ;

    public GenreServiceImpl(GenresRepository genresRepository, GenreValidator genreValidator) {
        this.genresRepository = genresRepository;
        this.genreValidator = genreValidator;
    }

    @Override
    public List<Genre> fetchAll() {
        try {
            return genresRepository.findAll();
        }catch (Exception e){
            log.error(e.getMessage());
            return List.of();
        }
    }

    @Override
    public Optional<Genre> getDetails(Long id) {
        try {
            return genresRepository.findById(id);
        }catch (Exception e){
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Genre save(CreateGenreRequestDTO createGenreRequestDTO) {
        Genre genre = createGenreFromDTO(createGenreRequestDTO);

        genreValidator.validate(genre);

        return genresRepository.save(genre);
    }

    @Override
    public Genre update(UpdateGenreRequestDTO updateGenreRequestDTO) {
        Optional<Genre> optionalGenre = genresRepository.findById(updateGenreRequestDTO.getId());
        if (optionalGenre.isPresent()) {
            Genre genre = optionalGenre.get();
            updateGenreFromDTO(genre, updateGenreRequestDTO);

            genreValidator.validate(genre);

            return genresRepository.save(genre);
        }
        log.error("Genre not found with id {}", updateGenreRequestDTO.getId());
        return null;
    }



    @Override
    public void delete(Long id) {
        try {
            Optional<Genre> optionalGenre = genresRepository.findById(id);

            if (optionalGenre.isEmpty()) {
                log.error("Gerne not found with id {}", id);
                return;
            }

            Genre genre = optionalGenre.get();
            genre.setDeleted(true);
            genresRepository.save(genre);
        } catch (Exception e) {
            log.error("Error soft deleting genre with id {}: {}", id, e.getMessage());
        }
    }

    @Override
    public List<Genre> fetchByIds(Set<Long> ids) {
        try {
            return genresRepository.findAllById(ids);
        }catch (Exception e){
            log.error(e.getMessage());
            return List.of();
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
