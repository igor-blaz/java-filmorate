package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;


@Slf4j
@Service
public class UserService {
    private final UserDbStorage userStorage;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addToFriends(int id, int newFriendId) {
        isRealUserId(List.of(id, newFriendId));
        log.info("Id прошли проверку");
        userStorage.addFriend(id, newFriendId);
    }

    public List<User> findCommonFriends(int firstId, int secondId) {
        isRealUserId(List.of(firstId, secondId));
        return userStorage.getCommonFriends(firstId, secondId);
    }

    public void deleteFriend(int userId, int friendToDelete) {
        isRealUserId(List.of(userId, friendToDelete));
        userStorage.removeFriend(userId, friendToDelete);
    }

    public List<User> getUserFriends(int id) {
        isRealUserId(List.of(id));
        log.info("Поиск друзей...");
        return userStorage.getFriends(id);
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        isRealUserId(List.of(user.getId()));
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void isRealUserId(List<Integer> ids) {
        userStorage.isRealUserId(ids);
    }
}
