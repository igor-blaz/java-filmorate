package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserLogDbStorage;
import ru.yandex.practicum.filmorate.model.UserLog;

import java.util.*;


@Slf4j
@Service
public class UserLogService {
    public final String EVENT_TYPE_USER = "USER";
    public final String EVENT_TYPE_LIKE = "LIKE";
    public final String EVENT_TYPE_REVIEW = "REVIEW";
    public final String EVENT_TYPE_FRIEND = "FRIEND";

    public final String EVENT_OPERATION_ADD = "ADD";
    public final String EVENT_OPERATION_UPDATE = "UPDATE";
    public final String EVENT_OPERATION_REMOVE = "REMOVE";

    private final UserLogDbStorage userLogDbStorage;

    @Autowired
    public UserLogService(UserLogDbStorage userLogDbStorage) {
        this.userLogDbStorage = userLogDbStorage;
    };

    public List<UserLog> getLogByUserId (long userId) {
        return userLogDbStorage.findByUserId(userId);
    }

    public void addUserLog(long userId, long entityId, String eventType, String operation) {
        UserLog userLog = new UserLog();

        userLog.setUserId(userId);
        userLog.setEntityId(entityId);
        userLog.setEventType(eventType);
        userLog.setOperation(operation);

        userLogDbStorage.insertUserLog(userLog);
    }
}
