package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String DELETE_BY_USER_ID_QUERY = "DELETE FROM users WHERE id = ?;";
    private static final String UPDATE_USER_BY_ID = "UPDATE users SET email = ?, " +
            "login = ?, name=?, birthday = ? WHERE id =?;";
    private static final String FIND_FRIENDS_ID_QUERY = "SELECT friend_id FROM " +
            "user_friends WHERE user_friend_id = ?;";
    private static final String DELETE_FRIEND_ID_QUERY = "DELETE FROM user_friends " +
            "WHERE user_id = ? AND friend_id = ? ;";
    private static final String FIND_COMMON_FRIENDS_ID_QUERY = """
            SELECT friend_id FROM user_friends WHERE user_id = ?
            INTERSECT
            SELECT friend_id FROM user_friends WHERE user_id = ?;
            """;
    private static final String ADD_TO_FRIENDS_QUERY= """
            INSERT INTO user_friends(user_id, friend_id, is_confirmed)
            VALUES(?,?,?);
            """;

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
        findOne(DELETE_BY_USER_ID_QUERY, user.getId()).
                orElseThrow(() -> new NotFoundException("Пользователь с ID " + user.getId() + " не найден"));
    }

    @Override
    public User updateUser(User user) {
        update(UPDATE_USER_BY_ID, user.getEmail(),
                user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
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

    public Set<User> getFriends(int id) {
        return new HashSet<>(findMany(FIND_FRIENDS_ID_QUERY));
    }


    public void addFriend(int id, int newFriend) {
         update(ADD_TO_FRIENDS_QUERY, id, newFriend);
    }

    public void removeFriend(int id, int deleteFriend) {
        update(DELETE_FRIEND_ID_QUERY, id, deleteFriend);
    }

    public List<User> getCommonFriends(int id, int friendId) {
        return findMany(FIND_COMMON_FRIENDS_ID_QUERY, id, friendId);
    }

    public List<User> getAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }


}
