package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RecordNotValidException;
import ru.yandex.practicum.filmorate.mappers.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, FilmDbStorage.class, FilmRowMapper.class,
        UserRowMapper.class, FriendRowMapper.class,
        MpaRowMapper.class, GenreRowMapper.class,
        GenreRowMapper.class, GenreDbStorage.class,
        MpaDbStorage.class
        })
public class FilmoRateApplicationTests {

    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private FilmDbStorage filmStorage;
    @Autowired
    private GenreDbStorage genreStorage;
    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    public void testFindUserById() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        Optional<User> userOptional = userStorage.findById(createdUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", createdUser.getId())
                );
    }

    @Test
    public void testFindAllUsers() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        List<User> users = userStorage.findAllUsers();

        assertThat(users)
                .extracting(User::getId)
                .contains(createdUser.getId());
    }

    @Test
    public void testCreateValidationException() {
        RecordNotValidException exception = assertThrows(RecordNotValidException.class, () -> {
            User user = new User();
            user.setEmail("test@mail.com");
            user.setLogin("");
            user.setName("Test Name");
            user.setBirthday(LocalDate.of(1990, 1, 1));

            userStorage.create(user);
        });

        assertEquals("Логин не может быть пустым.", exception.getMessage());
    }

    @Test
    public void testCreate() {
        User user = new User();
        user.setEmail("ivanov@mail.com");
        user.setLogin("ivanovLogin");
        user.setName("Ivanov Ivan");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        Optional<User> userOptional = userStorage.findById(createdUser.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("login", "ivanovLogin")
                );
    }

    @Test
    public void testUpdateNotIdException() {
        RecordNotValidException exception = assertThrows(RecordNotValidException.class, () -> {
            User user = new User();
            userStorage.update(user);
        });

        assertEquals("Id должен быть указан.", exception.getMessage());
    }

    @Test
    public void testUpdateNotFoundException() {
        User user = new User();
        user.setId(999L);
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userStorage.update(user);
        });

        assertEquals(String.format("Пользователь с id=%s не найден", user.getId()), exception.getMessage());
    }

    @Test
    public void testUpdateSuccess() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        User newUser = new User();
        newUser.setId(createdUser.getId());
        newUser.setEmail("petrov@gmail.com");
        newUser.setLogin("petrov-login");
        newUser.setName("Ivan Petrov");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User updatedUser = userStorage.update(newUser);

        Optional<User> selectedUser = userStorage.find(updatedUser.getId());

        assertThat(selectedUser)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("login", "petrov-login")
                );
    }

    @Test
    public void testShowPopularFilms(){
        Film film = new Film();
        film.setName("Test Test");
        film.setDescription("I love you");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(50);

        Film createdFilm = filmStorage.create(film);

        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        User secondUser = new User();
        secondUser.setEmail("test@mail.com");
        secondUser.setLogin("login");
        secondUser.setName("Test Name");
        secondUser.setBirthday(LocalDate.of(1990, 1, 1));

        User createdSecondUser = userStorage.create(secondUser);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());
        filmStorage.addLike(createdFilm.getId(), createdSecondUser.getId());

        List<Film> films = filmStorage.findPopularFilms(5);

        assertThat(films)
                .extracting(Film::getId)
                .contains(createdFilm.getId());
    }

    @Test
    public void testAddFriend() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        User friend = new User();
        friend.setEmail("test@mail.com");
        friend.setLogin("login");
        friend.setName("Test Name");
        friend.setBirthday(LocalDate.of(1990, 1, 1));

        User createdFriend = userStorage.create(friend);

        userStorage.addFriend(createdUser.getId(), createdFriend.getId());

        List<User> friends = userStorage.showFriends(createdFriend.getId());

        assertThat(friends)
                .isNotNull()
                .allSatisfy(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", createdFriend.getId());
                });
    }

    @Test
    public void testAddFriendUserNotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userStorage.addFriend(9999L, 9999L);
        });

        assertEquals(String.format("Пользователь с id=%s не найден", 9999L), exception.getMessage());
    }

    @Test
    public void testRemoveFriend() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        User friend = new User();
        friend.setEmail("test@mail.com");
        friend.setLogin("login");
        friend.setName("Test Name");
        friend.setBirthday(LocalDate.of(1990, 1, 1));

        User createdFriend = userStorage.create(friend);

        userStorage.addFriend(createdUser.getId(), createdFriend.getId());
    }


    @Test
    public void showFriends() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        User friend = new User();
        friend.setEmail("test@mail.com");
        friend.setLogin("login");
        friend.setName("Test Name");
        friend.setBirthday(LocalDate.of(1990, 1, 1));

        User createdFriend = userStorage.create(friend);

        userStorage.addFriend(createdUser.getId(), createdFriend.getId());

        List<User> friends = userStorage.showFriends(createdFriend.getId());

        assertThat(friends)
                .isNotNull()
                .allSatisfy(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", createdFriend.getId());
                });
    }

    @Test
    public void showCommonFriends() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        User friend = new User();
        friend.setEmail("test@mail.com");
        friend.setLogin("login");
        friend.setName("Test Name");
        friend.setBirthday(LocalDate.of(1990, 1, 1));

        User createdFriend = userStorage.create(friend);

        User secondFriend = new User();
        secondFriend.setEmail("aaa@mail.com");
        secondFriend.setLogin("test");
        secondFriend.setName("Test Name");
        secondFriend.setBirthday(LocalDate.of(1990, 1, 1));

        User secFriend = userStorage.create(secondFriend);

        userStorage.addFriend(createdUser.getId(), secFriend.getId());
        userStorage.addFriend(createdFriend.getId(), secFriend.getId());

        List<User> users = userStorage.showCommonFriends(createdUser.getId(), createdFriend.getId());

        assertThat(users)
                .isNotNull()
                .allSatisfy(u -> {
                    assertThat(u).hasFieldOrPropertyWithValue("id", secFriend.getId());
                });
    }

    @Test
    public void testFindAllFilms(){
        Film film = new Film();
        film.setName("Test Test");
        film.setDescription("I love you");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(50);

        Film createdUser = filmStorage.create(film);

        List<Film> films = filmStorage.findAllFilms();

        assertThat(films)
                .extracting(Film::getId)
                .contains(createdUser.getId());
    }

    @Test
    public void testFilmCreate() {
        Film film = new Film();
        film.setName("Test Test");
        film.setDescription("I love you");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(50);

        Film createdFilm = filmStorage.create(film);

        Optional<Film> filmOptional = filmStorage.findById(createdFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("description", "I love you")
                );
    }

    @Test
    public void testFilmCreateValidationException() {
        RecordNotValidException exception = assertThrows(RecordNotValidException.class, () -> {
            Film film = new Film();
            film.setName("");
            film.setDescription("I love you");
            film.setReleaseDate(LocalDate.of(1990, 1, 1));
            film.setDuration(50);

            filmStorage.create(film);
        });

        assertEquals("Название не может быть пустым.", exception.getMessage());
    }

    @Test
    public void testFilmUpdate(){
        Film film = new Film();
        film.setName("Test Test");
        film.setDescription("I love you");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(50);

        Film createdFilm = filmStorage.create(film);

        Film newFilm = new Film();
        newFilm.setId(createdFilm.getId());
        newFilm.setName("Random");
        newFilm.setDescription("Random Random");
        newFilm.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(60);
        Film updatedFilm = filmStorage.update(newFilm);

        Optional<Film> selectedFilm = filmStorage.find(updatedFilm.getId());

        assertThat(selectedFilm)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("description", "Random Random")
                );
    }

    @Test
    public void testAddLikeNotFoundException(){
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            filmStorage.addLike(9999L, 9999L);
        });

        assertEquals(String.format("Фильм с id=%s не найден", 9999L), exception.getMessage());
    }

    @Test
    public void testRemoveLike(){
        Film film = new Film();
        film.setName("Test Test");
        film.setDescription("I love you");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(50);

        Film createdFilm = filmStorage.create(film);

        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(user);

        filmStorage.addLike(createdFilm.getId(), createdUser.getId());

        Integer count = filmStorage.countLikes(createdFilm.getId());
        assertEquals(1, count);
    }

    @Test
    public void testFilmFindById() {
        Film film = new Film();
        film.setName("Test Test");
        film.setDescription("I love you");
        film.setReleaseDate(LocalDate.of(1990, 1, 1));
        film.setDuration(50);

        Film createdFilm = filmStorage.create(film);

        Optional<Film> filmOptional = filmStorage.findById(createdFilm.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(u ->
                        assertThat(u).hasFieldOrPropertyWithValue("id", createdFilm.getId())
                );
    }
}