package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Service
public class UserService {
    private final UserDbStorage userStorage;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Set<User> addToFriends(int id, int newFriendId) {
        userStorage.addFriend(id, newFriendId);
        return new HashSet<>(userStorage.getFriends(id));
    }

    public List<User> findCommonFriends(int firstId, int secondId) {
        return userStorage.getCommonFriends(firstId, secondId);
    }

    public void deleteFriend(int userId, int friendToDelete) {
        userStorage.removeFriend(userId, friendToDelete);
    }

    public Set<User> getUserFriends(int id) {
       return userStorage.getFriends(id);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void isRealUserId(List<Integer> ids) {
        userStorage.isRealUserId(ids);
    }
}
