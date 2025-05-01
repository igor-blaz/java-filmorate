package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbStorage filmStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final DirectorDbStorage directorDbStorage;
    private final UserLogService userLogService;

    public List<Film> getRatedFilms(int count, Integer genreId, Integer year) {
        return filmStorage.getTopRatedFilms(count, genreId, year);
    }

    public List<Film> setFieldsToArrayOfFilms(List<Film> films) {
        for (Film film : films) {
            findDirectors(film);
            findGenres(film);
            findMpa(film);
        }
        return films;
    }

    public Film setFieldsToOneFilm(Film film) {
        findDirectors(film);
        findMpa(film);
        findGenres(film);
        return film;
    }

    public Film makeLike(int filmId, int userId) {
        filmStorage.makeLike(filmId, userId);
        userLogService.addUserLog(userId, filmId, userLogService.EVENT_TYPE_FILM_LIKE, userLogService.EVENT_OPERATION_ADD);
        return filmStorage.getFilm(filmId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        filmStorage.deleteLike(filmId, userId);
        userLogService.addUserLog(userId, filmId, userLogService.EVENT_TYPE_FILM_LIKE, userLogService.EVENT_OPERATION_REMOVE);
    }

    public List<Film> getTopRatedFilms(int count, Integer genreId, Integer year) {
        return filmStorage.getTopRatedFilms(count, genreId, year);
    }

    public List<Film> searchBy(String query, String searchType) {
        if (searchType.isBlank()) {
            log.info("QueryString оказалась пустой");
            List<Integer> filmIds = filmStorage.findAllFilmIds();
            filmStorage.findManyFilmsByArrayOfIds(sortByLikes(filmIds));
        }
        switch (searchType) {
            case "title,director" -> {
                log.info("title,director");
                return searchByDirectorAndTitle(query);

            }
            case "director" -> {
                log.info("director");
                log.info("CASE DIRECTOR");
                return searchByDirector(query);
            }
            case "title" -> {
                log.info("title");
                return searchByTitle(query);
            }
        }
        return Collections.emptyList();

    }

    public List<Film> searchByDirectorAndTitle(String query) {
        List<Film> films = new ArrayList<>();
        List<Film> filmsByDirectorSearch = searchByDirector(query);
        List<Film> filmsByTitleSearch = searchByTitle(query);
        log.info("titleSearch{}", filmsByTitleSearch);
        log.info("dirSearch{}", filmsByDirectorSearch);

        films.addAll(filmsByTitleSearch);
        films.addAll(filmsByDirectorSearch);
        return films.stream().distinct().toList();

    }

    public List<Film> searchByDirector(String directorName) {
        log.info("searchByDirector");
        List<Integer> directorIds = directorDbStorage.findDirectorIdsByName(directorName);
        List<Integer> filmIds = new ArrayList<>();
        for (int directorId : directorIds) {
            List<Integer> ids = directorDbStorage.findFilmsByDirectorId(directorId);
            filmIds.addAll(ids);
        }
        List<Film> films = filmStorage.findManyFilmsByArrayOfIds(filmIds);
        findGenresForManyFilms(films);
        findDirectorsForManyFilms(films);
        return films;
    }

    public List<Film> searchByTitle(String title) {
        log.info("searchByTitle");
        List<Film> films = filmStorage.findFilmByNameLike(title);
        findGenresForManyFilms(films);
        return films;
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

        genreDbStorage.deleteGenresOnFilm(film.getId());
        genreDbStorage.insertManyGenres(film.getId(), film.getGenres());
        Set<Genre> genreSet = genreDbStorage.findGenresByFilmId(film.getId());
        film.setGenres(genreSet);
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

    private void findGenresForManyFilms(List<Film> films) {
        for (Film film : films) {
            findGenres(film);
        }
    }

    private void findDirectorsForManyFilms(List<Film> films) {
        for (Film film : films) {
            findDirectors(film);
        }
    }

    private void findGenres(Film film) {
        List<Integer> genresIds = filmStorage.getIdsForGenres(film.getId());
        Set<Genre> genres = new HashSet<>();
        for (int genreId : genresIds) {
            genres.add(genreDbStorage.getGenre(genreId));
        }
        film.setGenres(genres);
    }

    public void deleteFilmById(int idForDelete) {
        int deleteFilm = filmStorage.deleteFilm(idForDelete);
        if (deleteFilm == 0) {
            throw new NotFoundException("Фильм для удаления с id " + idForDelete + " не найден");
        }
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
            List<Integer> sortedIds = sortByLikes(filmIds);
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

    private List<Integer> sortByLikes(List<Integer> filmsIds) {
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