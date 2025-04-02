package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.controller.MinimumDate;

import java.time.LocalDate;
import java.util.*;


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
    @Size(min = 1, max = 200)
    private String description;
    @MinimumDate
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительной")
    private Integer duration;
    private List<Integer> mpa = new ArrayList<>();
    private List<Integer> genres = new ArrayList<>();

    public Film(String name, String description, LocalDate releaseDate, Integer duration,
                List<Integer> mpa, List<Integer> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
