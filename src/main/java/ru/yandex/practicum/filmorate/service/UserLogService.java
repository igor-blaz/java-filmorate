package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.UserLogDbStorage;
import ru.yandex.practicum.filmorate.model.UserLog;

import java.time.Instant;
import java.util.*;


@Slf4j
@Service
public class UserLogService {
    public static final String EVENT_TYPE_FILM_LIKE = "LIKE";
    public static final String EVENT_TYPE_REVIEW = "REVIEW";
    public static final String EVENT_TYPE_FRIEND = "FRIEND";

    public static final String EVENT_OPERATION_ADD = "ADD";
    public static final String EVENT_OPERATION_UPDATE = "UPDATE";
    public static final String EVENT_OPERATION_REMOVE = "REMOVE";

    private final UserLogDbStorage userLogDbStorage;
    private final UserDbStorage userDbStorage;

    @Autowired
    public UserLogService(UserLogDbStorage userLogDbStorage, UserDbStorage userDbStorage) {
        this.userLogDbStorage = userLogDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public List<UserLog> getLogByUserId(int userId) {
        userDbStorage.getUser(userId);
        return userLogDbStorage.findByUserId(userId);
    }

    public UserLog addUserLog(int userId, int entityId, String eventType, String operation) {
        /*String sql = "SELECT * FROM user_log WHERE user_id = ? AND entity_id = ? AND event_type = ? AND operation = ?";
        Optional<UserLog> userLogOptional = userLogDbStorage.findUserLog(sql, userId, entityId, eventType, operation);
        if (userLogOptional.isPresent()) {
            return userLogOptional.get();
        }*/

        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setTimestamp(Instant.now().toEpochMilli());
        userLog.setEntityId(entityId);
        userLog.setEventType(eventType);
        userLog.setOperation(operation);
        return userLogDbStorage.insertUserLog(userLog);
    }
}
