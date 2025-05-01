package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class UserLog {
    private Integer id;
    private long timestamp;
    private int userId;
    private int entityId;
    private String eventType;
    private String operation;

    public UserLog() {
    }

    public UserLog(int id, long timestamp, int userId, int entityId, String eventType, String operation) {
        this.id = id;
        this.timestamp = timestamp;
        this.userId = userId;
        this.entityId = entityId;
        this.eventType = eventType;
        this.operation = operation;
    }
}
