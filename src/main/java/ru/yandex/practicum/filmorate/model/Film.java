package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;


/**
 * Film.
 */
@Data
@NoArgsConstructor
@NonNull
public class Film {
    private int id = 0;
    @NotNull(message = "Название не должно быть null")
    private String name;
    @NotNull(message = "Описание не должно быть null")
    private String description;
    @NotNull(message = "Дата релиза не должна быть null")
    private LocalDate releaseDate;
    @NotNull(message = "Продолжительность не должна быть null")
    private Integer duration;


    public Film(int id, String name, String description, LocalDate releaseDate, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description, LocalDate releaseDate, Integer duration) {

        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }


}
