package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@Mapper(componentModel = "spring", uses = {GenreMapper.class, MpaMapper.class})
public interface FilmMapper {
    FilmDto toDto(Film film);
    Film toModel(FilmDto dto);
}
