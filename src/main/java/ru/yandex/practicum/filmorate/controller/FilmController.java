package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmservice;
    private final UserService userService;

    @PostMapping
    public Film addMovie(@Valid @RequestBody Film film) {
        log.info("Запрос на создание фильма");
        return filmservice.createFilm(film);
    }

    @PutMapping
    public Film updateMovie(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма{}", film);
        return filmservice.updateFilm(film);
    }

    @GetMapping
    public List<Film> getMovies() {
        return filmservice.getAllFilms();
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getPopularFromDirector(@PathVariable Integer directorId, @RequestParam String sortBy) {
        log.info("Запрос на сортировку");
        return filmservice.getPopularFromDirector(directorId, sortBy);
    }


    @GetMapping("/popular")
    public List<Film> getTopPopular(
            @RequestParam(defaultValue = "10") String count,
            @RequestParam(defaultValue = "0") String genreId,
            @RequestParam(defaultValue = "0") String year
    ) {
        log.info("Запрос на популярные фильмы");

        return filmservice.getTopRatedFilms(
                Integer.parseInt(count),
                Integer.parseInt(genreId),
                Integer.parseInt(year)
        );
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        filmservice.removeLike(id, userId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film makeLike(@PathVariable Integer id,
                         @PathVariable Integer userId) {
        userService.isRealUserId(List.of(userId));
        return filmservice.makeLike(id, userId);
    }


    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.info("Запрос на поиск фильма");
        return filmservice.getFilm(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("Запрос от {} на поиск общих фильмов с {}", userId, friendId);
        return filmservice.getCommonFilms(userId, friendId);
    }
}