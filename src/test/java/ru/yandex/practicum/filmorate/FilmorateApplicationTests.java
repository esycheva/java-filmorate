//package ru.yandex.practicum.filmorate;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@SpringBootTest
//class FilmorateApplicationTests {
//
//	@Test
//	void contextLoads() {
//	}
//
//	// *** Тесты для Film ***
//	@Test
//	public void shouldReturnErrorForSeveralBlankStringsInFilmName() {
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("   ");
//		film.setDescription("Это тест.");
//		film.setDuration(1);
//		film.setReleaseDate(LocalDate.now());
//
//		List<String> result = film.validateErrors();
//
//		assertTrue(result.contains("Название не может быть пустым."));
//	}
//
//	@Test
//	public void shouldReturnErrorForEmptyStringInFilmName() {
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("");
//		film.setDescription("Это тест.");
//		film.setDuration(1);
//		film.setReleaseDate(LocalDate.now());
//
//		List<String> result = film.validateErrors();
//
//		assertTrue(result.contains("Название не может быть пустым."));
//	}
//
//	@Test
//	public void shouldReturnErrorForNullValueInFilmName() {
//		Film film = new Film();
//		film.setId(144L);
//		film.setName(null);
//		film.setDescription("Это тест.");
//		film.setDuration(1);
//		film.setReleaseDate(LocalDate.now());
//
//		List<String> result = film.validateErrors();
//
//		assertTrue(result.contains("Название не может быть пустым."));
//	}
//
//	@Test
//	public void shouldNotReturnErrorOnNormalValueFilmName() {
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("Форрест Гамп.");
//		film.setDescription("Это тест.");
//		film.setDuration(1);
//		film.setReleaseDate(LocalDate.now());
//
//		List<String> result = film.validateErrors();
//
//		assertFalse(result.contains("Название не может быть пустым."));
//	}
//
//	@Test
//	public void shouldReturnErrorFor201ValueInFilmDescription() {
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("Форрест Гамп.");
//		film.setDescription("O".repeat(201));
//		film.setDuration(1);
//		film.setReleaseDate(LocalDate.now());
//
//		List<String> result = film.validateErrors();
//
//		assertTrue(result.contains("Максимальная длина описания 200 символов."));
//	}
//
//	@Test
//	public void shouldNotReturnErrorFor199ValueInFilmDescription() {
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("Форрест Гамп.");
//		film.setDescription("O".repeat(199));
//		film.setDuration(1);
//		film.setReleaseDate(LocalDate.now());
//
//		List<String> result = film.validateErrors();
//
//		assertFalse(result.contains("Максимальная длина описания 200 символов."));
//	}
//
//	@Test
//	public void shouldReturnErrorFor_01_01_1800_ValueInFilmReleaseDate() {
//		LocalDate date = LocalDate.of(1800, 1, 1);
//
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("Форрест Гамп.");
//		film.setDescription("Хороший фильм.");
//		film.setDuration(120);
//		film.setReleaseDate(date);
//
//		List<String> result = film.validateErrors();
//
//		assertTrue(result.contains("Дата выпуска не раньше 28 декабря 1895 года."));
//	}
//
//	@Test
//	public void shouldReturnErrorFor_27_12_1895_ValueInFilmReleaseDate() {
//		LocalDate date = LocalDate.of(1895, 12, 27);
//
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("Форрест Гамп.");
//		film.setDescription("Хороший фильм.");
//		film.setDuration(120);
//		film.setReleaseDate(date);
//
//		List<String> result = film.validateErrors();
//
//		assertTrue(result.contains("Дата выпуска не раньше 28 декабря 1895 года."));
//	}
//
//	@Test
//	public void shouldNotReturnErrorFor_30_12_1895_ValueInFilmReleaseDate() {
//		LocalDate date = LocalDate.of(1895, 12, 30);
//
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("Форрест Гамп.");
//		film.setDescription("Хороший фильм.");
//		film.setDuration(120);
//		film.setReleaseDate(date);
//
//		List<String> result = film.validateErrors();
//
//		assertFalse(result.contains("Дата выпуска не раньше 28 декабря 1895 года."));
//	}
//
//	@Test
//	public void shouldNotReturnErrorFor_20_02_2026_ValueInFilmReleaseDate() {
//		LocalDate date = LocalDate.of(2026, 02, 20);
//
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("Форрест Гамп.");
//		film.setDescription("Хороший фильм.");
//		film.setDuration(120);
//		film.setReleaseDate(date);
//
//		List<String> result = film.validateErrors();
//
//		assertFalse(result.contains("Дата выпуска не раньше 28 декабря 1895 года."));
//	}
//
//	@Test
//	public void shouldReturnErrorForNegativeValueInFilmDuration() {
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("Форрест Гамп.");
//		film.setDescription("Хороший фильм.");
//		film.setDuration(-1);
//		film.setReleaseDate(LocalDate.now());
//
//		List<String> result = film.validateErrors();
//
//		assertTrue(result.contains("Продолжительность должна быть больше нуля."));
//	}
//
//	@Test
//	public void shouldNotReturnErrorPositiveValueInFilmDuration() {
//		Film film = new Film();
//		film.setId(144L);
//		film.setName("Форрест Гамп.");
//		film.setDescription("Хороший фильм.");
//		film.setDuration(60);
//		film.setReleaseDate(LocalDate.now());
//
//		List<String> result = film.validateErrors();
//
//		assertFalse(result.contains("Продолжительность должна быть больше нуля."));
//	}
//
//	// *** Тесты для User ***
//
//	@Test
//	public void shouldReturnErrorForSeveralBlankStringsInUserEmail() {
//		LocalDate date = LocalDate.of(1980, 3, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("    ");
//		user.setLogin("test");
//		user.setName("");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertTrue(result.contains("Электронная почта не может быть пустой."));
//	}
//
//	@Test
//	public void shouldReturnErrorForEmptyStringInUserEmail() {
//		LocalDate date = LocalDate.of(1980, 3, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("");
//		user.setLogin("test");
//		user.setName("");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertTrue(result.contains("Электронная почта не может быть пустой."));
//	}
//
//	@Test
//	public void shouldReturnErrorForNullValueInUserEmail() {
//		LocalDate date = LocalDate.of(1980, 3, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail(null);
//		user.setLogin("test");
//		user.setName("");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertTrue(result.contains("Электронная почта не может быть пустой."));
//	}
//
//	@Test
//	public void shouldReturnErrorIfUserEmailNotContainsAtSign() {
//		LocalDate date = LocalDate.of(1980, 3, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("testgmail.com");
//		user.setLogin("test");
//		user.setName("");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertTrue(result.contains("Электронная почта должна содержать @."));
//	}
//
//	@Test
//	public void shouldNotReturnErrorIfUserEmailContainsAtSign() {
//		LocalDate date = LocalDate.of(1980, 3, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("test@gmail.com");
//		user.setLogin("test");
//		user.setName("");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertFalse(result.contains("Электронная почта должна содержать @."));
//	}
//
//	@Test
//	public void shouldReturnErrorForSeveralBlankStringsInUserLogin() {
//		LocalDate date = LocalDate.of(1980, 3, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("test@gmail.com");
//		user.setLogin("    ");
//		user.setName("");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertTrue(result.contains("Логин не может быть пустым."));
//	}
//
//	@Test
//	public void shoulReturnErrorForEmptyStringInUserLogin() {
//		LocalDate date = LocalDate.of(1980, 3, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("test@gmail.com");
//		user.setLogin("");
//		user.setName("");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertTrue(result.contains("Логин не может быть пустым."));
//	}
//
//	@Test
//	public void shouldReturnErrorForNullValueInUserLogin() {
//		LocalDate date = LocalDate.of(1980, 3, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("test@gmail.com");
//		user.setLogin(null);
//		user.setName("");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertTrue(result.contains("Логин не может быть пустым."));
//	}
//
//	@Test
//	public void shouldReturnLoginIfNameIsBlank() {
//		LocalDate date = LocalDate.of(1980, 3, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("test@gmail.com");
//		user.setLogin("test");
//		user.setName(null);
//		user.setBirthday(date);
//
//		String result = user.getName();
//
//		assertTrue(result.equals("test"));
//	}
//
//	@Test
//	public void shouldReturnErrorIfBirthdayInFuture() {
//		LocalDate date = LocalDate.of(2026, 12, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("test@gmail.com");
//		user.setLogin("test");
//		user.setName("Lena");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertTrue(result.contains("Дата рождения не может быть в будущем."));
//	}
//
//	@Test
//	public void shouldNotReturnErrorIfBirthdayInPast() {
//		LocalDate date = LocalDate.of(1986, 12, 12);
//		User user = new User();
//		user.setId(12L);
//		user.setEmail("test@gmail.com");
//		user.setLogin("test");
//		user.setName("Lena");
//		user.setBirthday(date);
//
//		List<String> result = user.validateErrors();
//
//		assertFalse(result.contains("Дата рождения не может быть в будущем."));
//	}
//}
