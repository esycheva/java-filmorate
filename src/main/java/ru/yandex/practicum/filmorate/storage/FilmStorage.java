package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public interface FilmStorage {

    public Collection<Film> findAllFilms();

    public Film create(Film film);

    public Film update(Film newFilm);

    public Optional<Film> addLike(Long filmId, Long userId);

    public Optional<Film> removeLike(Long filmId, Long userId);

    public List<Film> findPopularFilms(Integer count);

    public Optional<Film> findById(Long filmId);
}
