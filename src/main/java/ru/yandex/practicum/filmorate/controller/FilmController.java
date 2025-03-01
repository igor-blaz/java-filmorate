package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmservice;
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmController(FilmService filmservice, InMemoryFilmStorage inMemoryFilmStorage) {
        this.filmservice = filmservice;
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    @PostMapping
    public Film addMovie(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateMovie(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    @GetMapping
    public List<Film> getMovies() {
        return inMemoryFilmStorage.getAllFilms();
    }

    @GetMapping("/popular")
    public List<Film> getTopPopular(@RequestParam(defaultValue = "10") int count) {
        return filmservice.getTopRatedFilms(count);
    }

    @DeleteMapping
    public Film deleteLike(@PathVariable Integer id,
                           @PathVariable Integer userId) {
        return filmservice.removeLike(id, userId);
    }

    @PutMapping
    public Film makeLike(@PathVariable Integer id,
                         @PathVariable Integer userId) {
        return filmservice.makeLike(id, userId);
    }
}



