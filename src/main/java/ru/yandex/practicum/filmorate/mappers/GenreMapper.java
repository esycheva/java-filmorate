package ru.yandex.practicum.filmorate.mappers;


import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreDto toDto(Genre genre);
    Genre toEntity(GenreDto genreDto);

    List<GenreDto> toDtoList(Collection<Genre> genres);
}

