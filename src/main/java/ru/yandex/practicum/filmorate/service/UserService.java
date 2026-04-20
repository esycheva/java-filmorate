package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage storage;
    private final UserMapper userMapper;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage, UserMapper userMapper) {
        this.storage = storage;
        this.userMapper = userMapper;
    }

    public Optional<UserDto> findById(long userId) {
        return storage.findById(userId)
                .map(userMapper::toDto);
    }

    public Collection<UserDto> findAllUsers() {
        return storage.findAllUsers().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<User> addToFriends(Long id, Long friendId) {
        return storage.addFriend(id, friendId);
    }

    public Optional<User> removeFromFriends(Long id, Long friendId) {
        return storage.removeFriend(id, friendId);
    }

    public List<UserDto> showFriends(Long id) {
        return storage.showFriends(id).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> showCommonFriends(Long id, Long otherUserId) {
        return storage.showCommonFriends(id, otherUserId)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User newUser) {
        return storage.update(newUser);
    }

    public Optional<User> find(Long id) {
        return storage.find(id);
    }
}
