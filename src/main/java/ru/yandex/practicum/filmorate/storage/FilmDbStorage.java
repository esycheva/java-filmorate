package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RecordNotValidException;
import ru.yandex.practicum.filmorate.extractor.FilmExtractor;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmRowMapper mapper;
    private final UserRowMapper userMapper;
    private final MpaRowMapper mpaMapper;
    private final GenreRowMapper genreMapper;

    public List<Film> findAllFilms() {
        String query = "SELECT * FROM films";
        return jdbc.query(query, mapper);
    }

    public Film create(Film film) {
        if (film.validateErrors().size() > 0) {
            String str = film.validateErrors()
                    .stream()
                    .collect(Collectors.joining(","));
            throw new RecordNotValidException(str);
        }

        Mpa mpa = film.getMpa();

        if (mpa != null) {
            Optional<Mpa> selectedMpa = findMpa(mpa.getId());

            selectedMpa.orElseThrow(() -> new NotFoundException(String.format("MPA с id=%s не найден", mpa.getId())));
        }

        String sql = "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            return ps;
        }, keyHolder);

        Long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);

        List<Genre> genres = film.getGenres();

        if (genres != null) {
            List<Genre> uniqueGenres = genres.stream()
                    .distinct()
                    .toList();

            for(Genre genre: uniqueGenres) {
                Optional<Genre> selectedGenre = findGenre(genre.getId());
                selectedGenre.orElseThrow(() -> new NotFoundException(String.format("Жанр с id=%s не найден", genre.getId())));

                String genreSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                KeyHolder genreHolder = new GeneratedKeyHolder();

                jdbc.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(genreSql, new String[]{"id"});
                    ps.setLong(1, filmId);
                    ps.setLong(2, genre.getId());
                    return ps;
                }, genreHolder);
            }
        }

        if (mpa != null) {
            String mpaSql = "INSERT INTO film_mpa (film_id, mpa_id) VALUES (?, ?)";
            KeyHolder mpaHolder = new GeneratedKeyHolder();

            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(mpaSql, new String[]{"id"});
                ps.setLong(1, filmId);
                ps.setLong(2, mpa.getId());
                return ps;
            }, mpaHolder);
        }
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId().equals(null)) {
            throw new RecordNotValidException("Id должен быть указан.");
        }

        Optional<Film> film = find(newFilm.getId());

        film.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", newFilm.getId())));

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";

        jdbc.update(sql,
                newFilm.getName(),
                newFilm.getDescription(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getId());
        return newFilm;
    }

    public Optional<Film> addLike(Long filmId, Long userId) {
        Optional<Film> optFilm = find(filmId);

        Optional<User> user = findUser(userId);

        optFilm.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", filmId)));
        user.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));

        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, optFilm.get().getId());
            ps.setLong(2, userId);
            return ps;
        }, keyHolder);

        return optFilm;
    }

    public Integer countLikes(Long filmId) {
        Optional<Film> optFilm = find(filmId);

        optFilm.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", filmId)));

        String query = "SELECT COUNT(film_likes.film_id) FROM film_likes WHERE film_likes.film_id = ?";
        Integer count = jdbc.queryForObject(query, Integer.class, filmId);
        return count;
    }

    public Optional<Film> removeLike(Long filmId, Long userId){
        Optional<Film> optFilm = find(filmId);

        Optional<User> user = findUser(userId);

        optFilm.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", filmId)));
        user.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
        String query = "DELETE FROM film_likes " +
                "WHERE film_likes.film_id  = ?" +
                "AND film_likes.user_id = ?";
        jdbc.update(query, filmId, userId);
        return optFilm;
    }

    public List<Film> findPopularFilms(Integer count){
        if (count == null) {
            count = 10;
        }

        String query = "SELECT * " +
                "FROM films AS f " +
                "WHERE f.id IN (" +
                "   SELECT fl.film_id " +
                "   FROM FILM_LIKES AS fl " +
                "   GROUP BY (fl.film_id) " +
                "   ORDER BY COUNT(fl.film_id) DESC " +
                ") LIMIT ?";
        return jdbc.query(query, mapper, count);
    }

    public Optional<Film> findById(Long filmId) {
        return find(filmId);
    }

    public Optional<Film> find(Long id) {
        String sql = "SELECT f.*, " +
                "g.id AS genre_id, g.name AS genre_name, " +
                "m.id AS mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT OUTER JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT OUTER JOIN film_mpa AS fm ON f.id = fm.film_id " +
                "LEFT OUTER JOIN mpa AS m ON m.id = fm.mpa_id " +
                "WHERE f.id = ?";
        try {
//            Film film = jdbc.queryForObject(sql, mapper, id);
            Optional<Film> film = jdbc.query(sql, new FilmExtractor(), id);
            return film;
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<User> findUser(Long id) {
        String sql = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(sql, userMapper, id);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<Mpa> findMpa(Long id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        try {
            Mpa mpa = jdbc.queryForObject(sql, mpaMapper, id);
            return Optional.of(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<Genre> findGenre(Long id) {
        String sql = "SELECT id, name FROM genre WHERE id = ?";
        try {
            Genre genre = jdbc.queryForObject(sql, genreMapper, id);
            return Optional.of(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
