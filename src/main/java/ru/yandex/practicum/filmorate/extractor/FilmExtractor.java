package ru.yandex.practicum.filmorate.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreInFilmMapper;
import ru.yandex.practicum.filmorate.mappers.MpaInFilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

@Component
public class FilmExtractor implements ResultSetExtractor<Optional<Film>> {
    private FilmRowMapper filmMapper;

    private GenreInFilmMapper genreMapper;

    private MpaInFilmMapper mpaMapper;

    @Autowired
    public FilmExtractor(FilmRowMapper filmMapper, GenreInFilmMapper genreMapper, MpaInFilmMapper mpaMapper) {
        this.filmMapper = filmMapper;
        this.genreMapper = genreMapper;
        this.mpaMapper = mpaMapper;
    }

    @Override
    public Optional<Film> extractData(ResultSet rs) throws SQLException {
        Film film = null;

        while (rs.next()) {
            if (film == null) {
                // Используем маппер для создания основного объекта из первой строки
                film = filmMapper.mapRow(rs, 0);
            }

            // Проверяем наличие жанра в текущей строке
            if (rs.getLong("genre_id") != 0) {
                if (film.getGenres() == null) {
                    film.setGenres(new ArrayList<>());
                }

                film.getGenres().add(genreMapper.mapRow(rs, 0));
            }

            if (rs.getLong("mpa_id") != 0) {
                film.setMpa(mpaMapper.mapRow(rs, 0));
            }
        }

        return Optional.ofNullable(film);
    }
}
