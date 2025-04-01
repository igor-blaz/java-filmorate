package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String DELETE_BY_USER_ID_QUERY = "DELETE FROM users WHERE id = ?;";


    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper mapper) {
        super(jdbcTemplate, mapper);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(User user) {
        findOne(DELETE_BY_USER_ID_QUERY, user.getId());
    }

    @Override
    public User updateUser(User user) {
        return null;
    }
    public User getUser(int id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    public void isRealUserId(List<Integer> userIds) {
        List<User> users = findMany(FIND_BY_ID_QUERY, userIds.toArray());

        if (users.size() != userIds.size()) {
            for (int i = 0; i < userIds.size(); i++) {
                if (i >= users.size() || users.get(i) == null) {
                    throw new NotFoundException("Аккаунт с ID " + userIds.get(i) + " не найден");
                }
            }
        }
    }


    public List<User> getAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }


}
