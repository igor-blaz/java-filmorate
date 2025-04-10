package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> userMap = new HashMap<>();
    private Integer id = 1;
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);


    @Override
    public User createUser(User user) {
        validation(user);
        user.setId(id++);
        userMap.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @Override
    public void deleteUser(User user) {
        userMap.remove(user.getId());
    }

    @Override
    public User updateUser(User user) {
        validation(user);
        if (!userMap.containsKey(user.getId())) {
            log.warn("ID пользователя не найден. Невозможно обновить");
            throw new NotFoundException("Аккаунт с таким ID не найден");
        }
        userMap.put(user.getId(), user);
        log.info("Данные пользователя успешно обновлены");
        return user;
    }

    public List<User> getAllUsers() {
        return userMap.values().stream().toList();
    }

    public User getUser(int id) {
        isRealUserId(List.of(id));
        return userMap.get(id);
    }

    public void isRealUserId(List<Integer> idS) {
        for (int id : idS) {
            if (!userMap.containsKey(id)) {
                throw new NotFoundException("Аккаунт с ID " + id + " не найден");
            }
        }
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
