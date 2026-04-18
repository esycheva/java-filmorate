package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RecordNotValidException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements  GenreStorage {
    private final JdbcTemplate jdbc;
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
        String sql = "SELECT id, name FROM genre WHERE id = ?";
        try {
            Genre genre = jdbc.queryForObject(sql, mapper, id);
            return Optional.of(genre);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Genre create(Genre genre) {
        String sql = "INSERT INTO genre (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, genre.getName());
            return ps;
        }, keyHolder);

        genre.setId(keyHolder.getKey().longValue());
        return genre;
    }
}
