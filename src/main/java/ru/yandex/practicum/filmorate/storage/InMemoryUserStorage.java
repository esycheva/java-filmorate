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

        optUser.get().addFriend(friendUserId);
        friendUser.get().addFriend(id);
        return optUser;
    }

    public Optional<User> removeFriend(Long id, Long friendUserId) {
        Optional<User> optUser = find(id);
        Optional<User> friendUser = find(friendUserId);

        optUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", id)));

        friendUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", friendUserId)));

        optUser.get().removeFriend(friendUserId);
        friendUser.get().removeFriend(id);

        return optUser;
    }

    public List<User> showFriends(Long id) {
        Optional<User> optUser = find(id);

        if (optUser.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }

        User user = optUser.get();
        Set<Long> friends = user.getFriends();
        return users.values()
                .stream()
                .filter(u -> friends.contains(u.getId()))
                .toList();
    }

    public List<User> showCommonFriends(Long id, Long otherUserId) {
        Optional<User> optUser = find(id);
        Optional<User> optOtherUser = find(otherUserId);

        optUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", id)));

        optOtherUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", optOtherUser)));

        User user = optUser.get();
        User otherUser = optOtherUser.get();

        Set<Long> friends = user.getFriends();
        Set<Long> otherFriends = otherUser.getFriends();

        // friends are only common
        friends.retainAll(otherFriends);

        return users.values()
                .stream()
                .filter(u -> friends.contains(u.getId()))
                .toList();
    }

    public Collection<User> findAllUsers() {
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
        Set<Long> friends = new HashSet<>();
        user.setFriends(friends);
        users.put(user.getId(), user);
        return user;
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
        return find(userId);
    }

    private Long getNextId() {
        Long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Optional<User> find(Long id) {
        return users.values().stream()
                .filter(film -> film.getId().equals(id))
                .findFirst();
    }
}
