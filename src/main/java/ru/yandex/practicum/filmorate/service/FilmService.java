package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FilmService {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final DirectorDbStorage directorDbStorage;


    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, GenreDbStorage genreDbStorage,
                       MpaDbStorage mpaDbStorage, DirectorDbStorage directorDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
        this.directorDbStorage = directorDbStorage;
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
        log.info("Режиссеры фильма на добавление {}", film.getDirectors());
        findNamesForDirectors(film);
        Mpa mpa = findNameForMpa(film.getMpa());
        film.setMpa(mpa);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        directorDbStorage.deleteDirectorsOnFilm(film.getId());
        directorDbStorage.insertManyDirectors(film.getId(), film.getDirectors());
        Set<Director> directorSet = directorDbStorage.findDirectorsByFilmId(film.getId());
        film.setDirectors(directorSet);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int id) {
        Film film = filmStorage.getFilm(id);
        findMpa(film);
        findGenres(film);
        findDirectors(film);
        return film;
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId).stream()
                .peek(this::findGenres)
                .peek(this::findMpa)
                .toList();
    }

    private void findNamesForGenres(Film film) {
        film.setGenres(genreDbStorage.getManyGenres(film.getGenres()));
    }

    private void findDirectors(Film film) {
        log.info("Поиск Режиссера");
        Set<Director> directorSet = directorDbStorage.findDirectorsByFilmId(film.getId());
        film.setDirectors(directorSet);
    }

    private void findNamesForDirectors(Film film) {
        Set<Director> directors = directorDbStorage.findManyDirectorsById(film.getDirectors());
        film.setDirectors(directors);
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

    public List<Film> getPopularFromDirector(int directorId, String sortType) {
        List<Integer> filmIds = directorDbStorage.findFilmsByDirectorId(directorId);
        log.info("Айди, которые надо отсортировать {}", filmIds);
        if (sortType.equals("year")) {
            List<Film> films = filmStorage.findManyFilmsByArrayOfIds(filmIds);
            List<Film> sorted = sortByYear(films);
            directorDbStorage.setDirectorsForListOfFilms(sorted);
            return sorted;
        } else if (sortType.equals("likes")) {
            log.info("Сортировка по лайкам");
            List<Integer> sortedIds = sortIt(filmIds);
            List<Film> sortedFilms = filmStorage.findManyFilmsByArrayOfIds(sortedIds);
            directorDbStorage.setDirectorsForListOfFilms(sortedFilms);
            log.info("Отправка назад {}", sortedFilms);
            return sortedFilms;
        } else {
            throw new NotFoundException("Некорректная форма сортировки");
        }
    }

    private List<Film> sortByYear(List<Film> films) {
        return films.stream()
                .sorted(Comparator.comparing(Film::getReleaseDate))
                .toList();
    }

    private List<Integer> sortIt(List<Integer> filmsIds) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Integer filmId : filmsIds) {
            int likeCount = filmStorage.findLikesForFilm(filmId).size();
            map.put(filmId, likeCount);
        }
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }
}