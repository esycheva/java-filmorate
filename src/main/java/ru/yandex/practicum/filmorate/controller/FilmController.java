package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/films")
public class FilmController {

	private final FilmService service;
	private final UserService userService;

	private static final Logger log = LoggerFactory.getLogger(FilmController.class);

	public FilmController(FilmService service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}

	@GetMapping
	public Collection<Film> findAllFilms() {
		return service.findAllFilms();
	}

	@GetMapping("/{filmId}")
	public Optional<Film> findById(@PathVariable long filmId) {
		return service.findById(filmId);
	}

	@PostMapping
	public Film create(@Valid @RequestBody Film film) {
		Film createdFilm = service.create(film);
		log.info("Создан фильм {}.", createdFilm.getName());
		return createdFilm;
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film newFilm) {
		Film oldFilm = service.update(newFilm);

		log.info("Обновлён фильм с идентификатором {}.", oldFilm.getId());

		return oldFilm;
	}

	@PutMapping("/{id}/like/{userId}")
	public ResponseEntity<Film> addLike(@PathVariable Long id, @PathVariable Long userId) {
		Optional<User> optUser = userService.find(userId);

		optUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));

		Optional<Film> optFilm = service.addLike(id, userId);

        return optFilm.map(film -> ResponseEntity
                .status(HttpStatus.OK)
                .body(film)).orElseGet(() -> ResponseEntity
                .notFound().build());
    }

	@DeleteMapping("/{id}/like/{userId}")
	public ResponseEntity<Film> removerLike(@PathVariable Long id, @PathVariable Long userId) {
		Optional<User> optUser = userService.find(userId);
		
		optUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));

		Optional<Film> optFilm = service.removeLike(id, userId);

		optFilm.orElseThrow(() -> new NotFoundException(String.format("Фильм с id=%s не найден", id)));

		return optFilm.map(film -> ResponseEntity
				.status(HttpStatus.OK)
				.body(film)).orElseGet(() -> ResponseEntity
				.notFound().build());
	}

	@GetMapping("/popular")
	public List<Film> findPopularFilms(@RequestParam Integer count) {
		return service.showPopular(count);
	}
}
