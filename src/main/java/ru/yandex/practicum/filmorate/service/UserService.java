package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
// Добавление в друзья, удаление, вывод списка общих друзей
// не одобряем заявки, а принимаем автоматически. Если 1 друг 2, то 2 друг 1
//Set <long>

@Service
public class UserService {
    private InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Set<Integer> addToFriends(int id, int newFriendId) {
        User user = inMemoryUserStorage.getUser(id);
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
