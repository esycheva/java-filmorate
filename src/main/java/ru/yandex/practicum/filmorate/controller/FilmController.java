package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.RecordNotValidException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpHeaders;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

@RestController
@RequestMapping("/films")
public class FilmController {
	private final FilmStorage storage = new InMemoryFilmStorage();

	private final FilmService service = new FilmService(storage);

	private static final Logger log = LoggerFactory.getLogger(FilmController.class);

	@GetMapping
	public Collection<Film> findAllFilms() {
		return storage.findAllFilms();
	}

	@GetMapping("/{filmId}")
	public Optional<Film> findById(@PathVariable long filmId) {
		return service.findById(filmId);
	}

	@PostMapping
	public Film create(@Valid @RequestBody Film film) {
		Film createdFilm = storage.create(film);
		log.info("Создан фильм {}.", createdFilm.getName());
		return createdFilm;
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film newFilm) {
		Film oldFilm = storage.update(newFilm);

		log.info("Обновлён фильм с идентификатором {}.", oldFilm.getId());

		return oldFilm;
	}

	@PutMapping("/{id}/like/{userId}")
	public ResponseEntity<Film> addLike(@PathVariable Long id, @PathVariable Long userId){
		Optional<Film> optFilm = service.addLike(id, userId);

        return optFilm.map(film -> ResponseEntity
                .status(HttpStatus.CREATED)
                .body(film)).orElseGet(() -> ResponseEntity
                .notFound().build());
    }

	@DeleteMapping("/{id}/like/{userId}")
	public ResponseEntity<Film> removerLike(@PathVariable Long id, @PathVariable Long userId) {
		Optional<Film> optFilm = service.removeLike(id, userId);

		return optFilm.map(film -> ResponseEntity
				.status(HttpStatus.CREATED)
				.body(film)).orElseGet(() -> ResponseEntity
				.notFound().build());
	}

	@GetMapping("/popular?count={count}")
	public List<Film> findPopularFilms(@PathVariable Integer count){
		return service.showPopular(count);
	}
}
