package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLog {
    private Integer eventId;
    private long timestamp;
    private int userId;
    private int entityId;
    private String eventType;
    private String operation;
}
