package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;


    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addToFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.addToFriends(id, friendId);
        return userService.getUserFriends(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int friendId) {
        return userService.findCommonFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    public User deleteUsers(@PathVariable int id) {
        return userService.deleteUser(id);
    }
}
