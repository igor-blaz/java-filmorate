package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;


/**
 * Film.
 */
@Data
@NoArgsConstructor
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;


    public Film(int id, String name, String description, LocalDate releaseDate, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description, LocalDate releaseDate, Duration duration) {

        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }


}
