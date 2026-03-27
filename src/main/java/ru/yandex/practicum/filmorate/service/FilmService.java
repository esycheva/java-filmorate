package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Optional<Film> findById(long filmId) {
        return filmStorage.findById(filmId);
    }

    public Optional<Film> addLike(Long filmId, Long userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Optional<Film> removeLike(Long filmId, Long userId) {
        return filmStorage.removeLike(filmId, userId);
    }

    public List<Film> showPopular(Integer count) {
        return filmStorage.findPopularFilms(count);
    }
}
