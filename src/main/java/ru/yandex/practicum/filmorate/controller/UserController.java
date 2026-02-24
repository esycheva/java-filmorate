package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RecordNotValidException;

import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;

@RestController
public class UserController {
	private final Map<Long, User> users = new HashMap<>();
	private final static Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping
	public Collection<User> findAllFilms (){
		return users.values();
	}

	@PostMapping
	public User create(@Valid @RequestBody User user) {

		if(user.validateErrors().size() > 0) {
			String str = user.validateErrors()
				.stream()
				.collect(Collectors.joining(","));

			log.error("Произошла ошибка валадации пользователя c id={}: {}", user.getId(), str);

			throw new RecordNotValidException(str);
		}

		user.setId(getNextId());

		users.put(user.getId(), user);

		log.info("Создан пользователь с логином {}.", user.getLogin());

		return user;
	}

	@PutMapping
	public User update(@Valid @RequestBody User newUser) {

		if (newUser.getId().equals(null)) {
			log.error("Id должен быть указан.");
			throw new RecordNotValidException("Id должен быть указан.");
		}

		if (users.containsKey(newUser.getId())) {
			User oldUser = users.get(newUser.getId());

			if(newUser.validateErrors().size() > 0) {
				String str = newUser.validateErrors()
					.stream()
					.collect(Collectors.joining(","));

				log.error("Произошла ошибка валадации пользователя c id={}: {}", newUser.getId(), str);
				throw new RecordNotValidException(str);
			}

			oldUser.setEmail(newUser.getEmail());
			oldUser.setLogin(newUser.getLogin());
			oldUser.setName(newUser.getName());
			oldUser.setBirthday(newUser.getBirthday());

			log.info("Обновлён пользователь с идентификатором {}.", oldUser.getId());

			return oldUser;	
		}

		throw new NotFoundException(String.format("Пользователь с id=%s не найден.", newUser.getId()));
	}

	private Long getNextId() {
		Long currentMaxId = users.keySet()
				.stream()
				.mapToLong(id -> id)
				.max()
				.orElse(0);
		return ++currentMaxId;
	}
}
