package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

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

    public void deleteUser(int idForDelete) {
        int deleteUser = userStorage.deleteUser(idForDelete);
        if (deleteUser == 0) {
            throw new NotFoundException("Пользователь с ID " + idForDelete + " для удаления не найден");
        }
    }

    public User getUserById(int idOfUser) {
        return userStorage.getUser(idOfUser);
    }

    public List<Film> findRecommendation(Integer idUser) {
        return userStorage.findRecommendation(idUser);
    }
}
