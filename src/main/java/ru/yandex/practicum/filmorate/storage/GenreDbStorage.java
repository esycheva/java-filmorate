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
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements  GenreStorage {
    private final NamedParameterJdbcTemplate jdbc;
    private final GenreRowMapper mapper;

    public List<Genre> findAllGenres() {
        String query = "SELECT * FROM genre";
        return jdbc.query(query, mapper);
    }

    public Optional<Genre> findById(Long genreId) {
        Optional<Genre> genre = find(genreId);

        genre.orElseThrow(() -> new NotFoundException(String.format("Жанр с id=%s не найден", genreId)));

        return find(genreId);
    }

    public Optional<Genre> find(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

        String sql = "SELECT id, name FROM genre WHERE id = :id";
        try {
            Genre genre = jdbc.queryForObject(sql, namedParameters, mapper);
            return Optional.of(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Genre create(Genre genre) {
        String sql = "INSERT INTO genre (name) VALUES (:name)";

        SqlParameterSource genreParams = new MapSqlParameterSource()
                .addValue("name", genre.getName());

        KeyHolder genreKeyHolder = new GeneratedKeyHolder();

        jdbc.update(sql, genreParams, genreKeyHolder, new String[]{"id"});

        genre.setId(genreKeyHolder.getKey().longValue());
        return genre;
    }
}
