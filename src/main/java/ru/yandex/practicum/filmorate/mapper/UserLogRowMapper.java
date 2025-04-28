package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserLog;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserLogRowMapper implements RowMapper<UserLog> {
    @Override
    public UserLog mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        UserLog userLog = new UserLog();
        userLog.setTimeStamp(resultSet.getTimestamp("action_timestamp").toLocalDateTime());
        userLog.setUserId(resultSet.getInt("user_id"));
        userLog.setEntityId(resultSet.getInt("entity_id"));
        userLog.setEventType(resultSet.getString("event_type"));
        userLog.setOperation(resultSet.getString("operation"));;
        return userLog;
    }
}
