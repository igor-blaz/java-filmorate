package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FilmService {

    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film makeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        film.addLike(userId);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        film.removeLike(userId);
        return film;
    }

    public List<Film> getTopRatedFilms(int count) {
        List<Film> topRatedFilms = new ArrayList<>();
        List<Film> allFilms = filmStorage.getRatedFilms();
        for (int i = 0; i <= count; i++) {
            topRatedFilms.add(allFilms.get(i));
        }
        return topRatedFilms;
    }

}
