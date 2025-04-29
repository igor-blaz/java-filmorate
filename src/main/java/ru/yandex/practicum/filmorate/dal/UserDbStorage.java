package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collections;
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
    private final FilmDbStorage filmDbStorage;


    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper mapper, FilmDbStorage filmDbStorage) {
        super(jdbcTemplate, mapper);
        this.filmDbStorage = filmDbStorage;
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

    public List<Film> findRecommendation(Integer idUser) {
        List<Film> films = new ArrayList<>();
        List<Integer> users = usersWithSimilarLikes(Long.valueOf(idUser));

        if (!users.isEmpty()) {
            List<Integer> recommendations = filmRecommendations(idUser, users);
            for (Integer id : recommendations) {
                films.add(filmDbStorage.getFilm(id));
            }
        }
        return films;
    }

    private List<Integer> usersWithSimilarLikes(Long userId) {
        final String sqlQuery = "SELECT fl.user_id FROM film_likes ul " +
                "JOIN film_likes fl ON (ul.film_id = fl.film_id AND ul.user_id != fl.user_id) " +
                "JOIN users u ON (fl.user_id != u.id) " +
                "WHERE ul.user_id = ? " +
                "GROUP BY fl.user_id " +
                "HAVING COUNT(fl.film_id) > 1 " +
                "ORDER BY COUNT(fl.film_id) DESC";

        return findManyIds(sqlQuery, userId);
    }

    private List<Integer> filmRecommendations(Integer userId, List<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        String inSql = String.join(",", Collections.nCopies(userIds.size(), "?"));
        final String sqlQuery = "SELECT fl.film_id FROM film_likes fl " +
                "WHERE fl.user_id IN (" + inSql + ") " +
                "AND fl.film_id NOT IN (SELECT ul.film_id FROM film_likes ul WHERE ul.user_id = ?)";

        List<Object> params = new ArrayList<>(userIds);
        params.add(userId);

        return findManyIds(sqlQuery, params.toArray());
    }
}
