package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.dal.UserLogDbStorage;
import ru.yandex.practicum.filmorate.model.UserLog;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLogService {
    public static final String EVENT_TYPE_FILM_LIKE = "LIKE";
    public static final String EVENT_TYPE_REVIEW = "REVIEW";
    public static final String EVENT_TYPE_FRIEND = "FRIEND";

    public static final String EVENT_OPERATION_ADD = "ADD";
    public static final String EVENT_OPERATION_UPDATE = "UPDATE";
    public static final String EVENT_OPERATION_REMOVE = "REMOVE";

    private final UserLogDbStorage userLogDbStorage;
    private final UserDbStorage userDbStorage;

    public List<UserLog> getLogByUserId(int userId) {
        userDbStorage.getUser(userId);
        return userLogDbStorage.findByUserId(userId);
    }

    public UserLog addUserLog(int userId, int entityId, String eventType, String operation) {
        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setTimestamp(Instant.now().toEpochMilli());
        userLog.setEntityId(entityId);
        userLog.setEventType(eventType);
        userLog.setOperation(operation);
        return userLogDbStorage.insertUserLog(userLog);
    }
}
