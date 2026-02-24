package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.exception.RecordNotValidException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import org.springframework.web.bind.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/films")
public class FilmController {
	private final Map<Long, Film> films = new HashMap<>();

	private final static Logger log = LoggerFactory.getLogger(FilmController.class);

	@GetMapping
	public Collection<Film> findAllFilms (){
		return films.values();
	}

	@PostMapping
	public Film create(@Valid @RequestBody Film film) {

		if(film.validateErrors().size() > 0) {
			String str = film.validateErrors()
				.stream()
				.collect(Collectors.joining(","));

			log.error("Произошла ошибка валадации фильма c id={}: {}", film.getId(), str);
			
			throw new RecordNotValidException(str);
		}

		film.setId(getNextId());

		films.put(film.getId(), film);

		log.info("Создан фильм {}.", film.getName());

		return film;
	}

	@PutMapping
	public Film update(@Valid @RequestBody Film newFilm) {

		if (newFilm.getId().equals(null)) {

			log.error("Id должен быть указан.");

			throw new RecordNotValidException("Id должен быть указан.");
		}

		if (films.containsKey(newFilm.getId())) {
			Film oldFilm = films.get(newFilm.getId());

			if(newFilm.validateErrors().size() > 0) {
				String str = newFilm.validateErrors()
					.stream()
					.collect(Collectors.joining(","));

				log.error("Произошла ошибка валадации фильма c id={}: {}", newFilm.getId(), str);

				throw new RecordNotValidException(str);
			}

			oldFilm.setName(newFilm.getName());
			oldFilm.setDescription(newFilm.getDescription());
			oldFilm.setReleaseDate(newFilm.getReleaseDate());
			oldFilm.setDuration(newFilm.getDuration());

			log.info("Обновлён фильм с идентификатором {}.", oldFilm.getId());

			return oldFilm;	
		}

		throw new NotFoundException(String.format("Фильм с id=%s не найден.", newFilm.getId()));
	}
	
	private Long getNextId() {
		Long currentMaxId = films.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}
}
