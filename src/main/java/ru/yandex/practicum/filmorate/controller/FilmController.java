package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private Integer id = 1;
    private static final int MAX_DESCRIPTION = 200;
    private static final LocalDate OLDEST_FILM = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @PostMapping
    public Film addMovie(@Valid @RequestBody Film film) {
        film.setId(id++);
        filmMap.put(film.getId(), film);
        log.info("Фильм добавлен: id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film updateMovie(@Valid @RequestBody Film film) {
        if (!filmMap.containsKey(film.getId())) {
            log.warn("Нельзя обновить фильм, id которого нет");
            throw new ValidationException("Фильм с таким ID не найден");
        }
        filmMap.put(film.getId(), film);
        log.info("Фильм обновлен");
        return film;
    }

    @GetMapping
    public List<Film> getMovies() {
        log.info("Отправлены все фильмы");
        return new ArrayList<>(filmMap.values());
    }
}



