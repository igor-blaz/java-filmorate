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

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<Film> getRatedFilms(int count) {
        return filmStorage.getTopRatedFilms(count);
    }

    public Film makeLike(int filmId, int userId) {
        filmStorage.makeLike(filmId, userId);
        return filmStorage.getFilm(filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopRatedFilms(int count) {
        System.out.println("Service");
        return filmStorage.getTopRatedFilms(count);
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
        log.info("СМОТРИ {}", directorSet);


        log.info("ВАЖНО{}", directorSet);
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

    private void findNamesForGenres(Film film) {
        film.setGenres(genreDbStorage.getManyGenres(film.getGenres()));
    }

    private void findDirectors(Film film) {
        log.info("Поиск Режиссера");
        Set<Director> directorSet = directorDbStorage.findDirectorsByFilmId(film.getId());

        film.setDirectors(directorSet);
    }

    private void setDirectorsForManyFilms(List<Film> films, int directorId) {
        for (Film film : films) {
            Director director = new Director();

            findNamesForDirectors(film);
        }
        log.info("setDirectorsForMany {}", films);
    }

    private void findNamesForDirectors(Film film) {
        log.info("Поиск имени для режиссера");
        Set<Director>directors = directorDbStorage.findManyDirectorsById(film.getDirectors());
        log.info("ДИРЕКТОРА {}", directors);
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
        //directorDbStorage.isRealDirectorId(List.of(directorId));
        log.info("DIrector ID = {}", directorId);
        List<Integer> filmIds = directorDbStorage.findFilmsByDirectorId(directorId);
        log.info("АЙди фильмов {}", filmIds);
        List<Film> films = filmStorage.findManyFilmsByArrayOfIds(filmIds);
        log.info("Все фильмы {}", films); //Тут много фильмов без Directors
        for (Film film: films){
            Set<Director>directors = directorDbStorage.findDirectorsByFilmId(film.getId());
            film.setDirectors(directors);
            log.info("RRR {}", film.getDirectors()); //тут точно есть Directors
          //  directors.clear();
        }
       // setDirectorsForManyFilms(films, directorId);
        if (sortType.equals("year")) {
            log.info("jjjjjjjjjjjjjjjjjj {}", films);
            List<Film> sorted = sortByYear(films);
            log.info("Массив сортировки {}", sorted.stream().map(Film::getReleaseDate).collect(Collectors.toSet()));
            log.info("Массив сортировки {}", sorted.stream().map(Film::getDirectors).collect(Collectors.toSet()));
            return sorted;
        } else if (sortType.equals("likes")) {
            log.info("Сортировка по лайкам");
            List<Integer> ids = sortByLikes(filmIds);
            return filmStorage.findManyFilmsByArrayOfIds(ids);
        } else {
            throw new NotFoundException("Некорректная форма сортировки");
        }
    }

    private List<Film> sortByYear(List<Film> films) {
        log.info("SORTED {}", films);
        return films.stream()
                .sorted(Comparator.comparing(Film::getReleaseDate))
                .toList();
    }

    private List<Integer> sortByLikes(List<Integer> filmIds) {
        return filmStorage.findPopularFromArray(filmIds);
    }

}
