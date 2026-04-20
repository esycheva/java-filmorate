package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService service;

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping
	public Collection<UserDto> findAllUsers() {
		return service.findAllUsers();
	}

	@GetMapping("/{userId}")
	public Optional<UserDto> findById(@PathVariable long userId) {
		return service.findById(userId);
	}

	@PostMapping
	public User create(@Valid @RequestBody User user) {
		User createdUser = service.create(user);
		log.info("Создан пользователь с логином {}.", createdUser.getLogin());
		return createdUser;
	}

	@PutMapping
	public User update(@Valid @RequestBody User newUser) {
		User oldUser = service.update(newUser);
		log.info("Обновлён пользователь с идентификатором {}.", oldUser.getId());
		return oldUser;
	}

	@PutMapping("/{id}/friends/{friendId}")
	public ResponseEntity<User> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
		Optional<User> optUser = service.addToFriends(id, friendId);

		return optUser.map(user -> ResponseEntity
				.status(HttpStatus.OK)
				.body(user)).orElseGet(() -> ResponseEntity
				.notFound().build());
	}

	@DeleteMapping("/{id}/friends/{friendId}")
	public ResponseEntity<User> removerLike(@PathVariable Long id, @PathVariable Long friendId) {
		Optional<User> optUser = service.removeFromFriends(id, friendId);

		return optUser.map(film -> ResponseEntity
				.status(HttpStatus.OK)
				.body(film)).orElseGet(() -> ResponseEntity
				.notFound().build());
	}

	@GetMapping("/{id}/friends")
	public List<UserDto> showFriends(@PathVariable Long id) {
		return service.showFriends(id);
	}

	@GetMapping("/{id}/friends/common/{otherId}")
	public List<UserDto> findCommonFriends(@PathVariable Long id, @PathVariable  Long otherId) {
		return service.showCommonFriends(id, otherId);
	}
}
