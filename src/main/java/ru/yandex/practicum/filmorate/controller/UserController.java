package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
        log.info("Пользователь добавлен");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        validation(user);
        if (!userMap.containsKey(user.getId())) {
            log.warn("ID пользователя не найден. Невозможно обновить");
            throw new ValidationException("Аккаунт с таким ID не найден");
        }
        user.setId(id++);
        userMap.put(id, user);
        log.info("Данные пользователя успешно обновлены");
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Данные всех пользователей отправлены");
        return new ArrayList<>(userMap.values());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    public void validation(User user) {
        if (!user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.warn("(Validation)  Email не указан корректно");
            throw new ValidationException("Email не указан корректно");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("(Validation) Логин не указан корректно");
            throw new ValidationException("(Validation) Логин не указан корректно");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("(Validation) День рождения указан некорректно");
            throw new ValidationException("День рождения указан некорректно");
        }
        log.info("(Validation) Пользователь прошел валидацию");
    }

}
