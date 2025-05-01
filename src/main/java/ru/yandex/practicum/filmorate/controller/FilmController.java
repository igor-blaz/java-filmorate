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
        filmservice.createFilm(film);
        return filmservice.setFieldsToOneFilm(film);
    }

    @PutMapping
    public Film updateMovie(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма{}", film);
        filmservice.updateFilm(film);
        return filmservice.setFieldsToOneFilm(film);
    }

    @GetMapping("/search")
    public List<Film> searchFilmsBy(
            @RequestParam String query,
            @RequestParam String by) {
        log.info("Запрос на поиск query = {} by = {} ", query, by);
        List<Film> films = filmservice.searchBy(query, by);
        return filmservice.setFieldsToArrayOfFilms(films);
    }

    @GetMapping
    public List<Film> getMovies() {
        List<Film> films = filmservice.getAllFilms();
        return filmservice.setFieldsToArrayOfFilms(films);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getPopularFromDirector(@PathVariable Integer directorId, @RequestParam String sortBy) {
        log.info("Запрос на сортировку");
        List<Film> films = filmservice.getPopularFromDirector(directorId, sortBy);
        return filmservice.setFieldsToArrayOfFilms(films);
    }

    @GetMapping("/popular")
    public List<Film> getTopPopular(
            @RequestParam(defaultValue = "10") String count,
            @RequestParam(defaultValue = "0") String genreId,
            @RequestParam(defaultValue = "0") String year
    ) {
        log.info("Запрос на популярные фильмы");

        List<Film> films = filmservice.getTopRatedFilms(
                Integer.parseInt(count),
                Integer.parseInt(genreId),
                Integer.parseInt(year)
        );
        return filmservice.setFieldsToArrayOfFilms(films);
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
        Film film = filmservice.makeLike(id, userId);
        return filmservice.setFieldsToOneFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.info("Запрос на поиск фильма");
        Film film = filmservice.getFilm(id);
        return filmservice.setFieldsToOneFilm(film);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("Запрос от {} на поиск общих фильмов с {}", userId, friendId);
        List<Film> films = filmservice.getCommonFilms(userId, friendId);
        return filmservice.setFieldsToArrayOfFilms(films);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable int filmId) {
        filmservice.deleteFilmById(filmId);
    }
}