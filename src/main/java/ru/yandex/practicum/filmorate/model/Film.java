package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.controller.MinimumDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


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
    private Integer mpaId;

    public Film(String name, String description, LocalDate releaseDate, Integer duration, Integer mpaId) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpaId=mpaId;
    }

    public int getLikesSize() {
        return likersId.size();
    }

    public void addLike(int id) {
        likersId.add(id);
    }

    public void removeLike(int id) {
        likersId.remove(id);
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
