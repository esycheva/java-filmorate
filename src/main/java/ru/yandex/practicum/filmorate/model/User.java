package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

import jakarta.validation.constraints.Email;

@Data
public class User {
	private Long id;
	@Email
	private String email;
	private String login;
	private String name;
	private LocalDate birthday;
	private Set<Long> friends;

	public void addFriend(Long friendUserId) {
		Set<Long> friends = getFriends();

		if (friends == null) {
			friends = new HashSet<>();
			setFriends(friends);
		}
		if (!friends.contains(friendUserId)) {
			friends.add(friendUserId);
		}
	}

	public List<String> validateErrors() {
		List<String> errors = new ArrayList<>();

		if (email == null || email.isBlank() || email.isEmpty()) {
			errors.add("Электронная почта не может быть пустой.");
		}

		if (email != null && !email.contains("@")) {
			errors.add("Электронная почта должна содержать @.");
		}

		if (login == null || login.isBlank() || login.isEmpty()) {
			errors.add("Логин не может быть пустым.");
		}

		if (login != null && login.contains(" ")) {
			errors.add("Логин не должен содержать пробелы.");
		}

		if (birthday != null && birthday.isAfter(LocalDate.now())) {
			errors.add("Дата рождения не может быть в будущем.");
		}

		return errors;
	}

	public String getName() {
		if (name == null || name.isEmpty() || name.isBlank()) {
			return login;
		}
		return name;
	}
}