package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Service
public class FilmService {

    private final InMemoryFilmStorage filmStorage;
    private final InMemoryUserStorage userStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film makeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        film.addLike(userId);
        filmStorage.updateFilm(film);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        userStorage.isRealUserId(List.of(filmId));
        Film film = filmStorage.getFilm(filmId);
        film.removeLike(userId);
        filmStorage.updateFilm(film);
        return film;
    }

    public List<Film> getTopRatedFilms(int count) {
        List<Film> allFilms = new ArrayList<>(filmStorage.getRatedFilms());
        Collections.reverse(allFilms);
        if (count >= allFilms.size()) {
            return allFilms;
        }
        return allFilms.subList(0, count);
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
