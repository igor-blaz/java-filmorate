package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Service
public class FilmService {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getRatedFilms(int count) {
        return filmStorage.getTopRatedFilms(count);
    }

    public Film makeLike(int filmId, int userId) {
        filmStorage.makeLike(filmId, userId);
        return filmStorage.getFilm(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        userStorage.isRealUserId(List.of(filmId));
        Film film = filmStorage.getFilm(filmId);
        filmStorage.deleteLike(filmId, userId);
        return film;
    }

    public List<Film> getTopRatedFilms(int count) {
        return filmStorage.getTopRatedFilms(count);
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }


}
