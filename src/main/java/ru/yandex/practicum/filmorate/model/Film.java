package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.controller.MinimumDate;

import java.time.LocalDate;


/**
 * Film.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id = 0;
    @NotBlank(message = "Название не должно быть null")
    private String name;
    @NotBlank(message = "Описание не должно быть null")
    private String description;
    @PastOrPresent(message = "Дата релиза не должна быть null")
    @MinimumDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительной")
    private Integer duration;

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

}
