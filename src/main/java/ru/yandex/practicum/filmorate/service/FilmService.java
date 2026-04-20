package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final FilmMapper filmMapper;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmMapper filmMapper) {
        this.filmStorage = filmStorage;
        this.filmMapper = filmMapper;
    }

    public Optional<FilmDto> findById(long filmId) {
        return filmStorage.findById(filmId)
               .map(filmMapper::toDto);
    }

    public Optional<Film> addLike(Long filmId, Long userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Optional<Film> removeLike(Long filmId, Long userId) {
        return filmStorage.removeLike(filmId, userId);
    }

    public List<FilmDto> showPopular(Integer count) {
        return filmStorage.findPopularFilms(count).stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> findAllFilms() {
        return filmStorage.findAllFilms().stream()
                .map(filmMapper::toDto)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }
}
