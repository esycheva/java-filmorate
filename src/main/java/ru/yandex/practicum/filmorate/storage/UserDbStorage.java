package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbc;
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

        String sql = "INSERT INTO users (email, login, birthday, name) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setDate(3, Date.valueOf(user.getBirthday()));
            ps.setString(4, user.getName());
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new RecordNotValidException("Id должен быть указан.");
        }

        Optional<User> user = find(newUser.getId());

        user.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", newUser.getId())));

        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        jdbc.update(sql,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                Date.valueOf(newUser.getBirthday()),
                newUser.getId());
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

        String sql = "INSERT INTO friends (user_id, friend_id, approved) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setLong(1, user.get().getId());
            ps.setLong(2, friend.get().getId());
            ps.setBoolean(3, false);
            return ps;
        }, keyHolder);

        return user;
    }

    public Optional<User> removeFriend(Long id, Long friendUserId) {
        Optional<User> optUser = find(id);
        Optional<User> friendUser = find(friendUserId);

        optUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", id)));

        friendUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", friendUserId)));
        String query = "DELETE FROM friends " +
                       "WHERE friends.user_id  = ?" +
                       "AND friends.friend_id = ?";
        jdbc.update(query, id, friendUserId);

        return optUser;
    }

    public List<User> showFriends(Long id) {
        Optional<User> user = find(id);

        user.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", id)));

        String query = "SELECT fu.id, fu.name, fu.login, fu.birthday FROM users AS u " +
                       "INNER JOIN friends AS f ON u.id = f.user_id " +
                       "INNER JOIN users AS fu ON f.friend_id = fu.id " +
                       "WHERE u.id = ?";
        return jdbc.query(query, mapper, id);
    }

    public List<User> showCommonFriends(Long id, Long otherUserId) {
        String query = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM friends AS fr " +
                "INNER JOIN users AS u ON fr.friend_id = u.id " +
                "WHERE fr.friend_id IN ( " +
                "    SELECT f.friend_id AS id " +
                "    FROM friends AS f" +
                "     WHERE f.user_id = ?" +
                ") AND fr.user_id = ?";
        return jdbc.query(query, mapper, id, otherUserId);
    }

    public Optional<User> findById(Long userId) {
        return find(userId);
    }

    public Optional<User> find(Long id) {
        String sql = "SELECT id, email, login, name, birthday FROM users WHERE id = ?";
        try {
            User user = jdbc.queryForObject(sql, mapper, id);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<Friend> findFriend(Long userId, Long friendUserId) {
        String sql = "SELECT id, user_id, friend_id, approved FROM friends WHERE user_id = ? AND friend_id = ?";
        try {
            Friend friend = jdbc.queryForObject(sql, friendMapper, userId, friendUserId);
            return Optional.of(friend);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
