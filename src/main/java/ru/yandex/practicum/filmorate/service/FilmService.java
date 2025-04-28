package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final UserLogService userLogService;

    public List<Film> getRatedFilms(int count) {
        return filmStorage.getTopRatedFilms(count);
    }

    public Film makeLike(int filmId, int userId) {
        filmStorage.makeLike(filmId, userId);
        userLogService.addUserLog(userId, filmId, userLogService.EVENT_TYPE_LIKE, userLogService.EVENT_OPERATION_ADD);
        return filmStorage.getFilm(filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        filmStorage.deleteLike(filmId, userId);
        userLogService.addUserLog(userId, filmId, userLogService.EVENT_TYPE_LIKE, userLogService.EVENT_OPERATION_REMOVE);
    }

    public List<Film> getTopRatedFilms(int count) {
        System.out.println("Service");
        return filmStorage.getTopRatedFilms(count);
    }

    public Film createFilm(Film film) {
        findNamesForGenres(film);
        Mpa mpa = findNameForMpa(film.getMpa());
        film.setMpa(mpa);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        findMpa(film);
        findGenres(film);
        return film;
    }

    private void findNamesForGenres(Film film) {
        film.setGenres(genreDbStorage.getManyGenres(film.getGenres()));
    }

    private void findMpa(Film film) {
        film.setMpa(mpaDbStorage.findById(film.getMpa().getId()));
    }

    public Mpa findNameForMpa(Mpa mpa) {
        return mpaDbStorage.findById(mpa.getId());
    }

    private void findGenres(Film film) {
        List<Integer> genresIds = filmStorage.getIdsForGenres(film.getId());
        Set<Genre> genres = new HashSet<>();
        for (int genreId : genresIds) {
            genres.add(genreDbStorage.getGenre(genreId));
        }
        film.setGenres(genres);
    }
}
