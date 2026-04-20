package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RecordNotValidException;
import ru.yandex.practicum.filmorate.mappers.FriendRowMapper;
import ru.yandex.practicum.filmorate.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    //private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate jdbc;
    private final UserRowMapper mapper;
    private final FriendRowMapper friendMapper;

    public List<User> findAllUsers() {
        String query = "SELECT * FROM users";
        return jdbc.query(query, mapper);
    }

    public User create(User user) {
        if (user.validateErrors().size() > 0) {
            String str = user.validateErrors()
                    .stream()
                    .collect(Collectors.joining(","));

            throw new RecordNotValidException(str);
        }

        String sql = "INSERT INTO users (email, login, birthday, name) VALUES (:email, :login, :birthday, :name)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("birthday", Date.valueOf(user.getBirthday()))
                .addValue("name", user.getName());

        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new RecordNotValidException("Id должен быть указан.");
        }

        Optional<User> user = find(newUser.getId());

        user.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", newUser.getId())));

        String sql = "UPDATE users SET email = :email, login = :login, name = :name, birthday = :birthday WHERE id = :id";

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", newUser.getEmail())
                .addValue("login", newUser.getLogin())
                .addValue("birthday", Date.valueOf(newUser.getBirthday()))
                .addValue("name", newUser.getName())
                .addValue("id", newUser.getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        return newUser;
    }

    public Optional<User> addFriend(Long id, Long friendUserId) {
        Optional<User> user = find(id);
        Optional<User> friend = find(friendUserId);

        user.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", id)));

        friend.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", friendUserId)));

        Optional<Friend> optFriend = findFriend(id, friendUserId);

        if (optFriend.isPresent()) {
          throw new RecordNotValidException(String.format("У пользователя с id=%s уже добавлен друг с id=%s", id, friendUserId));
        }

        String sql = "INSERT INTO friends (user_id, friend_id, approved) VALUES (:userId, :test, :approved)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", user.get().getId())
                .addValue("test", friend.get().getId())
                .addValue("approved", false);

        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        return user;
    }

    public Optional<User> removeFriend(Long id, Long friendUserId) {
        Optional<User> optUser = find(id);
        Optional<User> friendUser = find(friendUserId);

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("userId", id)
                .addValue("friendUserId", friendUserId);

        optUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", id)));

        friendUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", friendUserId)));
        String query = "DELETE FROM friends " +
                       "WHERE friends.user_id  = :userId " +
                       "AND friends.friend_id = :friendUserId ";
        jdbc.update(query, namedParameters);

        return optUser;
    }

    public List<User> showFriends(Long id) {
        Optional<User> user = find(id);

        user.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", id)));

        SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("id", id);

        String query = "SELECT fu.id, fu.name, fu.login, fu.birthday FROM users AS u " +
                       "INNER JOIN friends AS f ON u.id = f.user_id " +
                       "INNER JOIN users AS fu ON f.friend_id = fu.id " +
                       "WHERE u.id = :id";
        return jdbc.query(query, namedParameters, mapper);
    }

    public List<User> showCommonFriends(Long id, Long otherUserId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("otherUserId", otherUserId);

        String query = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM friends AS fr " +
                "INNER JOIN users AS u ON fr.friend_id = u.id " +
                "WHERE fr.friend_id IN ( " +
                "    SELECT f.friend_id AS id " +
                "    FROM friends AS f" +
                "     WHERE f.user_id = :id" +
                ") AND fr.user_id = :otherUserId";
        return jdbc.query(query, namedParameters, mapper);
    }

    public Optional<User> findById(Long userId) {
        return find(userId);
    }

    public Optional<User> find(Long id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", id);
        String sql = "SELECT id, email, login, name, birthday FROM users WHERE id = :id";
        try {
            User user = jdbc.queryForObject(sql, namedParameters, mapper);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<Friend> findFriend(Long userId, Long friendUserId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("friendId", friendUserId);

        String sql = "SELECT id, user_id, friend_id, approved FROM friends WHERE user_id = :userId AND friend_id = :friendId";
        try {
            Friend friend = jdbc.queryForObject(sql, namedParameters, friendMapper);
            return Optional.of(friend);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
