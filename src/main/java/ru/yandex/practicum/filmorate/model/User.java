package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class User {
    int id = 0;
    @NotBlank(message = "@Email не должен быть null")
    String email;
    @NotBlank(message = "логин не должен быть null")
    String login;
    @NotBlank(message = "Имя не должно быть null")
    String name;
    @Past(message = "День рождения не должно быть null")
    LocalDate birthday;

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
