package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class DirectorDbStorage extends BaseRepository<Director> {
    private static final String FIND_ALL_DIR_QUERY = "SELECT * FROM directors;";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE director_id = ?;";
    private static final String DELETE_FILM_DIRECTOR_QUERY = "DELETE FROM film_directors WHERE film_id = ?;";
    private static final String FIND_DIRECTOR_QUERY = "SELECT * FROM directors WHERE director_id = ?;";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO directors " +
            "(director_name) VALUES (?);";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE directors SET director_name = ? " +
            "WHERE director_id = ?;";
    private static final String FIND_FILM_ID_BY_DIRECTOR_QUERY = "SELECT film_id FROM film_directors " +
            "WHERE director_id = ?;";
    private static final String INSERT_FILM_DIRECTOR_QUERY = "INSERT INTO film_directors " +
            "(film_id, director_id) VALUES (?, ?);";
    private static final String FIND_DIRECTOR_BY_FILM_QUERY = "SELECT director_id FROM film_directors " +
            "WHERE film_id = ?;";
    private static final String FIND_DIRECTOR_BY_NAME_QUERY_PART = "SELECT director_id FROM directors " +
            "WHERE LOWER(director_name) LIKE LOWER";


    public Set<Director> findDirectorsByFilmId(int filmId) {
        log.info("Storage. Запрос  {}", findManyIds(FIND_DIRECTOR_BY_FILM_QUERY, filmId));
        Set<Director> directorSet = new HashSet<>();
        List<Integer> directorIds = findManyIds(FIND_DIRECTOR_BY_FILM_QUERY, filmId);
        for (int id : directorIds) {
            directorSet.add(findDirectorById(id));
        }
        return directorSet;
    }

    public List<Integer> findDirectorIdsByName(String partName) {
        log.info("Поиск айди режиссеров по имени");
        String name = apostropheLikeMaker(partName);
        String query = FIND_DIRECTOR_BY_NAME_QUERY_PART + "(" + name + ")" + ";";
        return findManyIds(query);
    }

    public void setDirectorsForListOfFilms(List<Film> films) {
        for (Film film : films) {
            Set<Director> directors = findDirectorsByFilmId(film.getId());
            film.setDirectors(directors);
        }

    }


    public DirectorDbStorage(JdbcTemplate jdbcTemplate, DirectorRowMapper mapper) {
        super(jdbcTemplate, mapper);
    }

    public void addDirectorToFilm(int filmId, int directorId) {
        update(INSERT_FILM_DIRECTOR_QUERY, filmId, directorId);
    }


    public List<Director> findAllDirectors() {
        return findMany(FIND_ALL_DIR_QUERY);
    }

    public List<Integer> findFilmsByDirectorId(int directorId) {
        findDirectorById(directorId);
        return findManyIds(FIND_FILM_ID_BY_DIRECTOR_QUERY, directorId);
    }

    public Set<Director> findManyDirectorsById(Set<Director> directorsWithoutName) {
        Set<Director> directors = new HashSet<>();
        for (Director director : directorsWithoutName) {
            int id = director.getId();
            directors.add(findDirectorById(id));
        }
        return directors;
    }

    public Director findDirectorById(int id) {
        log.info("SQL запрос на поиск режиссера");
        return findOne(FIND_DIRECTOR_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Режиссер с ID " + id + " не найден"));
    }


    public Director insertDirector(Director director) {
        if (isNameEmpty(director.getName())) {
            throw new ValidationException("Не указано имя режиссёра при попытке добавления");
        }
        int id = insert(INSERT_DIRECTOR_QUERY, director.getName());
        log.info("Присвоен id режиссеру {}", id);
        director.setId(id);
        return director;
    }

    public void insertManyDirectors(int filmId, Set<Director> directors) {
        for (Director dir : directors) {
            addDirectorToFilm(filmId, dir.getId());
        }
    }

    public void deleteDirectorsOnFilm(int filmId) {
        update(DELETE_FILM_DIRECTOR_QUERY, filmId);
    }

    public Director updateDirector(Director director) {
        isRealDirectorId(List.of(director.getId()));
        update(UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
        return director;
    }

    public void deleteDirector(int id) {
        isRealDirectorId(List.of(id));
        update(DELETE_DIRECTOR_QUERY, id);
    }

    public void isRealDirectorId(List<Integer> directorIds) {
        List<Director> directors = findMany(FIND_DIRECTOR_QUERY, directorIds.toArray());

        if (directors.size() != directorIds.size()) {
            for (int i = 0; i < directorIds.size(); i++) {
                if (i >= directors.size() || directors.get(i) == null) {
                    throw new NotFoundException("Режиссер с ID " + directorIds.get(i) + " не найден");
                }
            }
        }
    }

    private boolean isNameEmpty(String nameForCheck) {
        return nameForCheck == null || nameForCheck.isBlank();
    }
}
