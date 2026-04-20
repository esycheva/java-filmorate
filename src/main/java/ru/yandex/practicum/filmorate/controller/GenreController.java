package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService service;

    private static final Logger log = LoggerFactory.getLogger(GenreController.class);

    @GetMapping
    public Collection<GenreDto> findAllGenres() {
        return service.findAllGenres();
    }

    @GetMapping("/{genreId}")
    public Optional<GenreDto> findById(@PathVariable long genreId) {
        return service.findById(genreId);
    }
}
