package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbc;
    private final MpaRowMapper mapper;

    public List<Mpa> findAllMpa() {
        String query = "SELECT * FROM mpa";
        return jdbc.query(query, mapper);
    }

    public Optional<Mpa> findById(Long mpaId) {
        Optional<Mpa> mpa = find(mpaId);

        mpa.orElseThrow(() -> new NotFoundException(String.format("MPA с id=%s не найден", mpaId)));

        return find(mpaId);
    }

    public Optional<Mpa> find(Long id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        try {
            Mpa mpa = jdbc.queryForObject(sql, mapper, id);
            return Optional.of(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Mpa create(Mpa mpa) {
        String sql = "INSERT INTO mpa (name) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, mpa.getName());
            return ps;
        }, keyHolder);

        mpa.setId(keyHolder.getKey().longValue());
        return mpa;
    }
}
