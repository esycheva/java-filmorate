package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage){
        this.storage = storage;
    }

    public Optional<User> findById(long userId) {
        return storage.findById(userId);
    }

    public Optional<User> addToFriends(Long id, Long friendId){
        return storage.addFriend(id, friendId);
    }

    public Optional<User> removeFromFriends(Long id, Long friendId){
        return storage.removeFriend(id, friendId);
    }

    public List<User> showFriends(Long id) {
        return storage.showFriends(id);
    }

    public List<User> showCommonFriends(Long id, Long otherUserId){
        return storage.showCommonFriends(id, otherUserId);
    }
}
