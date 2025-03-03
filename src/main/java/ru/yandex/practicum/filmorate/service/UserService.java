package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Service
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Set<Integer> addToFriends(int id, int newFriendId) {
        User user = inMemoryUserStorage.getUser(id);
        User newFriend = inMemoryUserStorage.getUser(newFriendId);
        newFriend.addFriend(id);
        user.addFriend(newFriendId);
        return user.getFriends();
    }

    public List<User> findCommonFriends(int firstId, int secondId) {
        User firstUser = inMemoryUserStorage.getUser(firstId);
        User secondUser = inMemoryUserStorage.getUser(secondId);
        Set<Integer> secondUserFriends = secondUser.getFriends();
        List<User> commonFriends = new ArrayList<>();
        for (Integer userId : firstUser.getFriends()) {
            if (secondUserFriends.contains(userId)) {
                commonFriends.add(inMemoryUserStorage.getUser(userId));
            }
        }
        return commonFriends;
    }

    public void deleteFriend(int userId, int friendToDelete) {
        User user = inMemoryUserStorage.getUser(userId);
        User deleteUser = inMemoryUserStorage.getUser(friendToDelete);
        deleteUser.removeFriend(userId);
        user.removeFriend(friendToDelete);
    }

    public List<User> getUserFriends(int id) {
        User user = inMemoryUserStorage.getUser(id);
        Set<Integer> userFriends = user.getFriends();
        return userFriends
                .stream()
                .map(inMemoryUserStorage::getUser)
                .filter(Objects::nonNull)
                .toList();
    }
}
