package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RecordNotValidException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;

    @Autowired
    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
    }

    public Optional<Film> findById(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    public Optional<Film> addLike(Long filmId, Long userId) {
        Optional<Film> optFilm = find(filmId);

        optFilm.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", filmId)));

        Film film = optFilm.get();
        Set<Long> likes = film.getLikes();

        if (!likes.contains(userId)) {
            likes.add(userId);
        }
        return optFilm;
    }

    public Optional<Film> removeLike(Long filmId, Long userId) {
        Optional<Film> optFilm = find(filmId);

        optFilm.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", filmId)));

        Film film = optFilm.get();
        Set<Long> likes = film.getLikes();

        if (likes.contains(userId)) {
            likes.remove(userId);
        }
        return optFilm;
    }

    public List<Film> findPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }

        return films.values()
                .stream()
                .sorted(Comparator.comparingInt(Film::likeCount).reversed())
                .limit(count)
                .toList();
    }

    public List<Film> findAllFilms() {
        return films.values()
                .stream()
                .toList();
    }

    public Film create(Film film) {
        if (film.validateErrors().size() > 0) {
            String str = film.validateErrors()
                    .stream()
                    .collect(Collectors.joining(","));
            throw new RecordNotValidException(str);
        }

        film.setId(getNextId());

        Set<Long> likes = new HashSet<>();
        film.setLikes(likes);

        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId().equals(null)) {
            throw new RecordNotValidException("Id должен быть указан.");
        }

        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            if (newFilm.validateErrors().size() > 0) {
                String str = newFilm.validateErrors()
                        .stream()
                        .collect(Collectors.joining(","));

                throw new RecordNotValidException(str);
            }
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            return oldFilm;
        }
        throw new NotFoundException(String.format("Фильм с id=%s не найден.", newFilm.getId()));
    }

    private Long getNextId() {
        Long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Optional<Film> find(Long id) {
       return films.values().stream()
                .filter(film -> film.getId().equals(id))
                .findFirst();
    }
}
