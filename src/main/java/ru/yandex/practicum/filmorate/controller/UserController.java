package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final InMemoryUserStorage inMemoryUserStorage;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, InMemoryUserStorage inMemoryUserStorage) {
        this.userService = userService;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }


    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return inMemoryUserStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        inMemoryUserStorage.isRealUserId(List.of(user.getId()));
        return inMemoryUserStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public Set<Integer> addToFriends(@PathVariable int id, @PathVariable int friendId) {
        inMemoryUserStorage.isRealUserId(List.of(id, friendId));
        return userService.addToFriends(id, friendId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }


}
