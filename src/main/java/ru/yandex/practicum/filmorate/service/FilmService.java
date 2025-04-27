package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
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
public class FilmService {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;


    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, GenreDbStorage genreDbStorage,
                       MpaDbStorage mpaDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<Film> getRatedFilms(int count, Integer genreId, Integer year) {
        return filmStorage.getTopRatedFilms(count, genreId, year);
    }

    public Film makeLike(int filmId, int userId) {
        filmStorage.makeLike(filmId, userId);
        return filmStorage.getFilm(filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopRatedFilms(int count, Integer genreId, Integer year) {
        System.out.println("Service");
        return filmStorage.getTopRatedFilms(count, genreId, year);
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
