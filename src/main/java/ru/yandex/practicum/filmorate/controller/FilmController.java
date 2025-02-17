package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    Map<Integer, Film> filmMap = new HashMap<>();
    Integer id = 0;
    static final int MAX_DESCRIPTION = 200;
    static final LocalDate OLDEST_FILM = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addMovie(@RequestBody Film film) {
        validation(film);
        film.setId(id++);
        filmMap.put(id, film);
        return film;
    }

    @PutMapping
    public Film updateMovie(@RequestBody Film film) {
        validation(film);
        if (!filmMap.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким ID не найден");
        }
        film.setId(id++);
        filmMap.put(id, film);
        return film;
    }

    @GetMapping
    public List<Film> getMovies() {
        return new ArrayList<>(filmMap.values());
    }

    /*название не может быть пустым;
    максимальная длина описания — 200 символов;
    дата релиза — не раньше 28 декабря 1895 года;
    продолжительность фильма должна быть положительным числом.*/
    public void validation(Film film) {
        if (film.getName().isBlank() || film.getName().isEmpty()) {
            throw new ValidationException("Нет названия");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION) {
            throw new ValidationException("Название больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(OLDEST_FILM)) {
            throw new ValidationException("Дата релиза неверна");
        }
        if (film.getDuration().isNegative()) {
            throw new ValidationException("Продолжительность указана неверно");
        }
    }
}
