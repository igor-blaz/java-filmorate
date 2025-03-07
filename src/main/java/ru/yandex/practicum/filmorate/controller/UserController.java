package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {


    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer id = 1;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        validation(user);
        user.setId(id++);
        userMap.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validation(user);
        if (!userMap.containsKey(user.getId())) {
            log.warn("ID пользователя не найден. Невозможно обновить");
            throw new ValidationException("Аккаунт с таким ID не найден");
        }
        userMap.put(user.getId(), user);
        log.info("Данные пользователя успешно обновлены");
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Данные всех пользователей отправлены");
        return new ArrayList<>(userMap.values());
    }

    private void validation(User user) {

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("(Validation) Логин не указан корректно");
            throw new ValidationException("(Validation) Логин не указан корректно");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("(Validation) Пользователь прошел валидацию");
    }

}
