package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Service
public class UserService {
    private final UserDbStorage userStorage;

    @Autowired
    public UserService(UserDbStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Set<Integer> addToFriends(int id, int newFriendId) {
        User user = userStorage.getUser(id);
        User newFriend = userStorage.getUser(newFriendId);
        newFriend.addFriend(id);
        user.addFriend(newFriendId);
        return user.getFriends();
    }

    public List<User> findCommonFriends(int firstId, int secondId) {
        User firstUser = userStorage.getUser(firstId);
        User secondUser = userStorage.getUser(secondId);
        Set<Integer> secondUserFriends = secondUser.getFriends();
        List<User> commonFriends = new ArrayList<>();
        for (Integer userId : firstUser.getFriends()) {
            if (secondUserFriends.contains(userId)) {
                commonFriends.add(userStorage.getUser(userId));
            }
        }
        return commonFriends;
    }

    public void deleteFriend(int userId, int friendToDelete) {
        User user = userStorage.getUser(userId);
        User deleteUser = userStorage.getUser(friendToDelete);
        deleteUser.removeFriend(userId);
        user.removeFriend(friendToDelete);
    }

    public List<User> getUserFriends(int id) {
        User user = userStorage.getUser(id);
        Set<Integer> userFriends = user.getFriends();
        return userFriends
                .stream()
                .map(userStorage::getUser)
                .filter(Objects::nonNull)
                .toList();
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
