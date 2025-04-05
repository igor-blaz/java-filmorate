package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.trace("Новый пользователь добавляется");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friend_id}")
    public List<User> addToFriends(@PathVariable int id, @PathVariable int friend_id) {
        log.info("Запрос на добавление в друзья");
        userService.addToFriends(id, friend_id);
        return userService.getUserFriends(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}/friends/common/{friend_id}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int friend_id) {
        return userService.findCommonFriends(id, friend_id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        log.info("Запрос на получение друзей пользователя {}", id);
        return userService.getUserFriends(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }


}
