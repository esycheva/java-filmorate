package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    public Collection<User> findAllFilms();

    public User create(User user);

    public User update(User newUser);

    public Optional<User> addFriend(Long id, Long friendUserId);

    public Optional<User> removeFriend(Long id, Long friendUserId);

    public List<User> showFriends(Long id);

    public List<User> showCommonFriends(Long id, Long otherUserId);

    public Optional<User> findById(Long userId);
}
