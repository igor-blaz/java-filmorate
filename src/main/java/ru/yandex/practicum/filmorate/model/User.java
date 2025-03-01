package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
public class User {
    private int id = 0;
    @NotBlank(message = "@Email не должен быть пустым")
    @Email
    private String email;
    @NotBlank(message = "логин не должен быть пустым")
    private String login;
    private String name;
    @PastOrPresent(message = "День рождения не должно быть в будущем")
    private LocalDate birthday;
    private Set<Integer> friends;

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
    public Set<Integer>addFriend(int friendId){
        friends.add(friendId);
        return friends;
    }
    public void removeFriend(int friendId){
        friends.remove(friendId);
    }
}
