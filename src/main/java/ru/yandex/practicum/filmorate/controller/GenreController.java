package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService service;

    private static final Logger log = LoggerFactory.getLogger(GenreController.class);

    public GenreController(GenreService service) {
        this.service = service;
    }

    @GetMapping
    public Collection<Genre> findAllGenres() {
        return service.findAllGenres();
    }

    @GetMapping("/{genreId}")
    public Optional<Genre> findById(@PathVariable long genreId) {
        return service.findById(genreId);
    }
}
