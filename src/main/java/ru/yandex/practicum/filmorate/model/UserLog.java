package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserLog {
    private Integer id;
    private LocalDateTime timeStamp;
    private long userId;
    private long entityId;
    private String eventType;
    private String operation;

    public UserLog() {
    }

    public UserLog(long id, LocalDateTime timeStamp, long userId, long entityId, String eventType, String operation) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.operation = operation;
    }
}
