package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
public interface UserStorage {
    public List<User> findAllUsers();

    public User create(User user);

    public User update(User newUser);

    public Optional<User> addFriend(Long id, Long friendUserId);

    public Optional<User> removeFriend(Long id, Long friendUserId);

    public List<User> showFriends(Long id);

    public List<User> showCommonFriends(Long id, Long otherUserId);

    public Optional<User> findById(Long userId);

    public Optional<User> find(Long id);
}
