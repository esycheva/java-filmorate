package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final GenreStorage genreStorage;
    private final GenreMapper genreMapper;

    @Autowired
    public GenreService(@Qualifier("genreDbStorage") GenreStorage genreStorage, GenreMapper genreMapper) {
        this.genreStorage = genreStorage;
        this.genreMapper = genreMapper;
    }

    public Collection<GenreDto> findAllGenres() {
        return genreStorage.findAllGenres().stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<GenreDto> findById(long genreId) {
        return genreStorage.findById(genreId)
                .map(genreMapper::toDto);
    }
}
