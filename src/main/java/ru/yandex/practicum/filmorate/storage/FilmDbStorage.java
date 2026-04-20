package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final NamedParameterJdbcTemplate jdbc;
    private final FilmExtractor filmExtractor;
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

        String sql = "INSERT INTO films (name, description, release_date, duration) " +
                "VALUES (:name, :description, :releaseDate, :duration)";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", Date.valueOf(film.getReleaseDate()))
                .addValue("duration", film.getDuration());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        Long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);

        List<Genre> genres = film.getGenres();

        if (genres != null) {
            List<Genre> uniqueGenres = genres.stream()
                    .distinct()
                    .toList();

            for (Genre genre: uniqueGenres) {
                Optional<Genre> selectedGenre = findGenre(genre.getId());
                selectedGenre.orElseThrow(() -> new NotFoundException(String.format("Жанр с id=%s не найден", genre.getId())));

                String genreSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (:filmId, :genreId)";

                SqlParameterSource genreParams = new MapSqlParameterSource()
                        .addValue("filmId", filmId)
                        .addValue("genreId", genre.getId());

                KeyHolder genreKeyHolder = new GeneratedKeyHolder();

                jdbc.update(genreSql, genreParams, genreKeyHolder, new String[]{"id"});
            }
        }

        if (mpa != null) {
            String mpaSql = "INSERT INTO film_mpa (film_id, mpa_id) VALUES (:filmId, :mpaId)";

            SqlParameterSource genreParams = new MapSqlParameterSource()
                    .addValue("filmId", filmId)
                    .addValue("mpaId", mpa.getId());

            KeyHolder mpaKeyHolder = new GeneratedKeyHolder();

            jdbc.update(mpaSql, genreParams, mpaKeyHolder, new String[]{"id"});
        }
        return film;
    }

    public Film update(Film newFilm) {
        if (newFilm.getId().equals(null)) {
            throw new RecordNotValidException("Id должен быть указан.");
        }

        Optional<Film> film = find(newFilm.getId());

        film.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", newFilm.getId())));

        String sql = "UPDATE films SET name = :name, description = :description, release_date = :releaseDate, duration = :duration WHERE id = :id";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", newFilm.getName())
                .addValue("description", newFilm.getDescription())
                .addValue("releaseDate", Date.valueOf(newFilm.getReleaseDate()))
                .addValue("duration", newFilm.getDuration())
                .addValue("id", newFilm.getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        return newFilm;
    }

    public Optional<Film> addLike(Long filmId, Long userId) {
        Optional<Film> optFilm = find(filmId);

        Optional<User> user = findUser(userId);

        optFilm.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", filmId)));
        user.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));

        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (:filmId, :userId)";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("filmId", optFilm.get().getId())
                .addValue("userId", userId);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        return optFilm;
    }

    public Integer countLikes(Long filmId) {
        Optional<Film> optFilm = find(filmId);

        optFilm.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", filmId)));

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("filmId", filmId);

        String query = "SELECT COUNT(film_likes.film_id) FROM film_likes WHERE film_likes.film_id = :filmId";
        Integer count = jdbc.queryForObject(query, namedParameters, Integer.class);
        return count;
    }

    public Optional<Film> removeLike(Long filmId, Long userId) {
        Optional<Film> optFilm = find(filmId);

        Optional<User> user = findUser(userId);

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("filmId", filmId)
                .addValue("userId", userId);

        optFilm.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", filmId)));
        user.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
        String query = "DELETE FROM film_likes " +
                "WHERE film_likes.film_id  = :filmId " +
                "AND film_likes.user_id = :userId ";
        jdbc.update(query, namedParameters);
        return optFilm;
    }

    public List<Film> findPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("count", count);

        String query = "SELECT * " +
                "FROM films AS f " +
                "WHERE f.id IN (" +
                "   SELECT fl.film_id " +
                "   FROM FILM_LIKES AS fl " +
                "   GROUP BY (fl.film_id) " +
                "   ORDER BY COUNT(fl.film_id) DESC " +
                ") LIMIT :count";
        return jdbc.query(query, namedParameters, mapper);
    }

    public Optional<Film> findById(Long filmId) {
        return find(filmId);
    }

    public Optional<Film> find(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

        String sql = "SELECT f.*, " +
                "g.id AS genre_id, g.name AS genre_name, " +
                "m.id AS mpa_id, m.name AS mpa_name " +
                "FROM films AS f " +
                "LEFT OUTER JOIN film_genre AS fg ON f.id = fg.film_id " +
                "LEFT OUTER JOIN genre AS g ON g.id = fg.genre_id " +
                "LEFT OUTER JOIN film_mpa AS fm ON f.id = fm.film_id " +
                "LEFT OUTER JOIN mpa AS m ON m.id = fm.mpa_id " +
                "WHERE f.id = :id";
        try {
            Optional<Film> film = jdbc.query(sql, namedParameters, filmExtractor);
            return film;
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<User> findUser(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

        String sql = "SELECT id, email, login, name, birthday FROM users WHERE id = :id";
        try {
            User user = jdbc.queryForObject(sql, namedParameters, userMapper);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<Mpa> findMpa(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

        String sql = "SELECT id, name FROM mpa WHERE id = :id";
        try {
            Mpa mpa = jdbc.queryForObject(sql, namedParameters, mpaMapper);
            return Optional.of(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<Genre> findGenre(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);


        String sql = "SELECT id, name FROM genre WHERE id = :id";
        try {
            Genre genre = jdbc.queryForObject(sql, namedParameters, genreMapper);
            return Optional.of(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
