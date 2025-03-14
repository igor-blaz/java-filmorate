package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmservice;
    private final UserService userService;

    @PostMapping
    public Film addMovie(@Valid @RequestBody Film film) {
        return filmservice.createFilm(film);
    }

    @PutMapping
    public Film updateMovie(@Valid @RequestBody Film film) {
        return filmservice.updateFilm(film);
    }

    @GetMapping
    public List<Film> getMovies() {
        return filmservice.getAllFilms();
    }

    @GetMapping("/popular")
    public List<Film> getTopPopular(@RequestParam(defaultValue = "10") int count) {
        return filmservice.getTopRatedFilms(count);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        userService.isRealUserId(List.of(userId));
        return filmservice.removeLike(id, userId);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film makeLike(@PathVariable Integer id,
                         @PathVariable Integer userId) {
        userService.isRealUserId(List.of(userId));
        return filmservice.makeLike(id, userId);
    }
}



