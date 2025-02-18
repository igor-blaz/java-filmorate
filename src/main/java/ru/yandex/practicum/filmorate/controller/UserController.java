package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {


    Map<Integer, User> userMap = new HashMap<>();
    Integer id = 0;
    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public User addUser(@RequestBody User user) {
        validation(user);
        user.setId(id++);
        userMap.put(id, user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validation(user);
        if (!userMap.containsKey(user.getId())) {
            throw new ValidationException("Аккаунт с таким ID не найден");
        }
        user.setId(id++);
        userMap.put(id, user);
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    /*электронная почта не может быть пустой и должна содержать символ @;
    логин не может быть пустым и содержать пробелы;
    имя для отображения может быть пустым — в таком случае будет использован логин;
    дата рождения не может быть в будущем.*/
    public void validation(User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            throw new ValidationException("Email не указан корректно");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не указан корректно");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("День рождения указан некорректно");
        }
    }

}
