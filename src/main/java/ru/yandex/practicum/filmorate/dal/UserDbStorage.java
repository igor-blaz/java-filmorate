package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDbStorage extends BaseRepository<User> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String ADD_USER_QUERY = "INSERT INTO users (email, login, name," +
            " birthday) Values(?,?,?,?)";
    private static final String DELETE_BY_USER_ID_QUERY = "DELETE FROM users WHERE id = ?;";
    private static final String UPDATE_USER_BY_ID = "UPDATE users SET email = ?, " +
            "login = ?, name=?, birthday = ? WHERE id =?;";
    private static final String GET_FRIENDS_QUERY = """
                SELECT friend_id FROM user_friends WHERE user_id = ?
            """;
    private static final String DELETE_FRIEND_ID_QUERY = "DELETE FROM user_friends " +
            "WHERE user_id = ? AND friend_id = ? ;";
    private static final String FIND_COMMON_FRIENDS_ID_QUERY = """
            SELECT friend_id FROM user_friends WHERE user_id = ?
            INTERSECT
            SELECT friend_id FROM user_friends WHERE user_id = ?;
            """;
    private static final String ADD_TO_FRIENDS_QUERY = """
            INSERT INTO user_friends(user_id, friend_id)
            VALUES(?,?);
            """;
    private static final String REMOVE_USER = "DELETE FROM users WHERE id=?";


    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper mapper) {
        super(jdbcTemplate, mapper);
    }

    public User createUser(User user) {
        int generatedId = insert(ADD_USER_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return getUser(generatedId);
    }

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
        for (Integer userId : userIds) {
            Optional<User> user = findOne(FIND_BY_ID_QUERY, userId);

            if (user.isEmpty()) {
                throw new NotFoundException("Аккаунт с ID " + userId + " не найден");
            }
        }
    }

    public List<User> getFriends(int id) {
        List<Integer> friendsAsIds = findManyIds(GET_FRIENDS_QUERY, id);
        return integerToUserConverter(friendsAsIds);
    }

    public List<User> integerToUserConverter(List<Integer> ids) {
        List<User> friendsAsUsers = new ArrayList<>();
        for (Integer friendId : ids) {
            friendsAsUsers.add(findOne(FIND_BY_ID_QUERY, friendId)
                    .orElseThrow(() -> new NotFoundException("Пользователь с ID " + friendId + " не найден")));
        }
        return friendsAsUsers;
    }


    public void addFriend(int id, int newFriend) {
        update(ADD_TO_FRIENDS_QUERY, id, newFriend);
    }

    public void removeFriend(int id, int deleteFriend) {
        update(DELETE_FRIEND_ID_QUERY, id, deleteFriend);
    }

    public List<User> getCommonFriends(int id, int friendId) {
        List<Integer> friendsAsIds = findManyIds(FIND_COMMON_FRIENDS_ID_QUERY, id, friendId);
        return integerToUserConverter(friendsAsIds);
    }

    public List<User> getAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    public int deleteUser(int idUserForDelete) {
        return update(REMOVE_USER, idUserForDelete);
    }
}
