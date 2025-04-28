package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.UserLogDbStorage;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserLog;

import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userStorage;
    private final UserLogService userLogService;

    public void addToFriends(int id, int newFriendId) {
        isRealUserId(List.of(id, newFriendId));
        userStorage.addFriend(id, newFriendId);
        userLogService.addUserLog(id, newFriendId, userLogService.EVENT_TYPE_FRIEND, userLogService.EVENT_OPERATION_ADD);
    }

    public List<User> findCommonFriends(int firstId, int secondId) {
        isRealUserId(List.of(firstId, secondId));
        return userStorage.getCommonFriends(firstId, secondId);
    }

    public void deleteFriend(int userId, int friendToDelete) {
        isRealUserId(List.of(userId, friendToDelete));
        userStorage.removeFriend(userId, friendToDelete);
        userLogService.addUserLog(userId, friendToDelete, userLogService.EVENT_TYPE_FRIEND, userLogService.EVENT_OPERATION_REMOVE);
    }

    public List<User> getUserFriends(int id) {
        isRealUserId(List.of(id));
        return userStorage.getFriends(id);
    }

    public User createUser(User user) {
        User newUser = userStorage.createUser(user);
        userLogService.addUserLog(newUser.getId(), newUser.getId(), userLogService.EVENT_TYPE_USER, userLogService.EVENT_OPERATION_ADD);
        return newUser;
    }

    public User updateUser(User user) {
        isRealUserId(List.of(user.getId()));
        userLogService.addUserLog(user.getId(), user.getId(), userLogService.EVENT_TYPE_USER, userLogService.EVENT_OPERATION_UPDATE);
        return userStorage.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void isRealUserId(List<Integer> ids) {
        userStorage.isRealUserId(ids);
    }
}
