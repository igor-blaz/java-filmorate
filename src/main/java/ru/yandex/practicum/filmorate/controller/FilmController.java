package ru.yandex.practicum.filmorate.controller;

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
    private Integer id = 0;
    private static final int MAX_DESCRIPTION = 200;
    private static final LocalDate OLDEST_FILM = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @PostMapping
    public Film addMovie(@RequestBody Film film) {
        validation(film);
        film.setId(id++);
        filmMap.put(film.getId(), film);
        log.info("Фильм добавлен: id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film updateMovie(@RequestBody Film film) {
        validation(film);
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

    private void validation(Film film) throws ValidationException {

        if (film.getDescription().length() >= MAX_DESCRIPTION) {
            log.warn("(Validation) Название больше 200 символов");
            throw new ValidationException("Название больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(OLDEST_FILM)) {
            log.warn("(Validation) Дата релиза неверна");
            throw new ValidationException("Дата релиза неверна");
        }
        log.info("(Validation) Фильм прошел валидацию");
    }
}
