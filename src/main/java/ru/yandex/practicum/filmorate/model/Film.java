package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import lombok.Data;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Data
public class Film {
	private Long id;
	private String name;
	private String description;
	private LocalDate releaseDate;
	private Integer duration;
	private Set<Long> likes;
	private List<Genre> genres;
	private Mpa mpa;

	public Integer likeCount() {
		return likes.size();
	}

	public List<String> validateErrors() {
		List<String> errors = new ArrayList<>();

		if (name == null || name.isBlank() || name.isEmpty()) {
			errors.add("Название не может быть пустым.");
		}

		if (description != null && description.length() > 200) {
			errors.add("Максимальная длина описания 200 символов.");
		}

		LocalDate date = LocalDate.of(1895, 12, 28);

		if (releaseDate != null && releaseDate.isBefore(date)) {
			errors.add("Дата выпуска не раньше 28 декабря 1895 года.");
		}

		if (duration != null && duration < 0) {
			errors.add("Продолжительность должна быть больше нуля.");
		}
		return errors;
	}
}
