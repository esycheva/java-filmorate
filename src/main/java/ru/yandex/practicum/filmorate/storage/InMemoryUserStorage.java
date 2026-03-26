package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RecordNotValidException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;

    @Autowired
    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    public Optional<User> addFriend(Long id, Long friendUserId) {
        Optional<User> optUser = find(id);
        Optional<User> friendUser = find(friendUserId);

        optUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", id)));

        friendUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", friendUserId)));

        if (optUser.isPresent()) {
            User user = optUser.get();
            Set<Long> friends = user.getFriends();
            if (friends == null) {
                friends = new HashSet<>();
                user.setFriends(friends);
            }
            if (!friends.contains(friendUserId)) {
                friends.add(friendUserId);
            }
        }
        return optUser;
    }

    public Optional<User> removeFriend(Long id, Long friendUserId) {
        Optional<User> optUser = find(id);

        if (optUser.isPresent()) {
            User user = optUser.get();
            Set<Long> friends = user.getFriends();
            if (friends.contains(friendUserId)) {
                friends.remove(friendUserId);
            }
        }
        return optUser;
    }

    public List<User> showFriends(Long id) {
        Optional<User> optUser = find(id);

        if (optUser.isPresent()) {
            User user = optUser.get();
            Set<Long> friends = user.getFriends();
            return users.values()
                    .stream()
                    .filter(u -> friends.contains(u.getId()))
                    .toList();
        } else {
            return List.of();
        }
    }

    public List<User> showCommonFriends(Long id, Long otherUserId) {
        Optional<User> optUser = find(id);
        Optional<User> optOtherUser = find(otherUserId);

        if (optUser.isPresent() && optOtherUser.isPresent()) {
            User user = optUser.get();
            User otherUser = optOtherUser.get();

            Set<Long> friends = user.getFriends();
            Set<Long> otherFriends = otherUser.getFriends();

            return users.values()
                    .stream()
                    .filter(u -> friends.contains(u.getId()) && otherFriends.contains(u.getId()))
                    .toList();
        } else {
            return List.of();
        }
    }

    public Collection<User> findAllFilms() {
        return users.values();
    }

    public User create(User user) {
        if (user.validateErrors().size() > 0) {
            String str = user.validateErrors()
                    .stream()
                    .collect(Collectors.joining(","));

            throw new RecordNotValidException(str);
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public void delete() {

    }

    public User update(User newUser) {
        if (newUser.getId().equals(null)) {
            throw new RecordNotValidException("Id должен быть указан.");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (newUser.validateErrors().size() > 0) {
                String str = newUser.validateErrors()
                        .stream()
                        .collect(Collectors.joining(","));
                throw new RecordNotValidException(str);
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            return oldUser;
        }
        throw new NotFoundException(String.format("Пользователь с id=%s не найден.", newUser.getId()));
    }

    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    private Long getNextId() {
        Long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private Optional<User> find(Long id) {
        return users.values().stream()
                .filter(film -> film.getId().equals(id))
                .findFirst();
    }
}
