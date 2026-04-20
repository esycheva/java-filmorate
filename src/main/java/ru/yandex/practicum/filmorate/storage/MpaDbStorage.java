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
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component("mpaDbStorage")
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final NamedParameterJdbcTemplate jdbc;
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
        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

        String sql = "SELECT id, name FROM mpa WHERE id = :id";
        try {
            Mpa mpa = jdbc.queryForObject(sql, namedParameters, mapper);
            return Optional.of(mpa);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Mpa create(Mpa mpa) {
        String sql = "INSERT INTO mpa (name) VALUES (:name)";

        SqlParameterSource genreParams = new MapSqlParameterSource()
                .addValue("name", mpa.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(sql, genreParams, keyHolder, new String[]{"id"});

        mpa.setId(keyHolder.getKey().longValue());
        return mpa;
    }
}
