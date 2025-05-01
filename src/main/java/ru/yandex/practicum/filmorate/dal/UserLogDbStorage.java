package ru.yandex.practicum.filmorate.dal;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserLogRowMapper;
import ru.yandex.practicum.filmorate.model.UserLog;

import java.util.List;

@Slf4j
@Repository
public class UserLogDbStorage extends BaseRepository<UserLog> {

    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM user_log WHERE user_id = ?;";
    private static final String INSERT_USER_LOG = """
        INSERT INTO user_log (action_timestamp, user_id, entity_id, event_type, operation)
        VALUES(?, ?, ?, ?, ?);
        """;

    public UserLogDbStorage(JdbcTemplate jdbcTemplate, UserLogRowMapper userLogRowMapper) {
        super(jdbcTemplate, userLogRowMapper);
    }

    public UserLog insertUserLog(UserLog userLog) {
        int id = insert(
                INSERT_USER_LOG,
                userLog.getTimestamp(),
                userLog.getUserId(),
                userLog.getEntityId(),
                userLog.getEventType(),
                userLog.getOperation()
        );
        userLog.setEventId(id);
        return new UserLog(
                userLog.getEventId(),
                userLog.getTimestamp(),
                userLog.getUserId(),
                userLog.getEntityId(),
                userLog.getEventType(),
                userLog.getOperation());
    }

    public List<UserLog> findByUserId(long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }
}
