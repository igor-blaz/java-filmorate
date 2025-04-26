package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Repository
public class FilmDbStorage extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM film ";
    private static final String UPDATE_FILM_BY_ID = """
             UPDATE film SET name = ?,
             description = ?, release_date=?, duration = ?,
             mpa_id = ?
             WHERE id =?;
            """;

private static final String FIND_TOP_POPULAR_QUERY = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id
            FROM film f
            JOIN film_genre g ON g.film_id = f.id
            JOIN film_likes k ON k.film_id = f.id
            WHERE (? = 0 OR g.genre_id = ?)
              AND (? = 0 OR extract(year from f.release_date) = ?)
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id
            ORDER BY count(1) DESC
            LIMIT ?
            """;
    private static final String FIND_LIKES_FOR_FILM = "SELECT user_id FROM film_likes WHERE film_id = ? ;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String INSERT_FILM_VALUES = "INSERT INTO film " +
            "(name, description, release_date, duration, mpa_id) Values(?,?,?,?,?);";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) Values(?,?);";
    private static final String INSERT_FILM_DIRECTOR = "INSERT INTO film_directors (film_id, director_id) Values(?,?);";
    private static final String ADD_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_GENRES_BY_FILM = "SELECT genre_id FROM film_genre WHERE " +
            "film_id = ?  ORDER BY genre_id";
    private static final String GET_COMMON_FILMS = "SELECT * FROM film f WHERE f.id IN (" +
            "SELECT fl.film_id FROM film_likes fl " +
            "WHERE fl.user_id IN (?, ?) " +
            "GROUP BY fl.film_id " +
            "HAVING COUNT(user_id) = 2" +
            "ORDER BY COUNT(user_id) DESC)";

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper mapper) {
        super(jdbcTemplate, mapper);
    }

    public List<Film> findManyFilmsByArrayOfIds(List<Integer> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        String params = paramsMaker(ids.size());
        String findManyFilmsQuery = "SELECT * FROM film WHERE id IN (" + params + ")";
        return findMany(findManyFilmsQuery, ids.toArray());
    }

    private String paramsMaker(int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> "?")
                .collect(Collectors.joining(", "));
    }

    public void makeLike(int filmId, int userId) {
        update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        update(REMOVE_LIKE_QUERY, filmId, userId);
    }


    public void insertFilmAndGenre(int id, Set<Genre> genres) {
        List<Integer> genreIds = genres.stream().map(Genre::getId).toList();
        for (int genreId : genreIds) {
            update(INSERT_FILM_GENRE, id, genreId);
        }
    }

    public List<Integer> findLikesForFilm(int filmId) {
        return findManyIds(FIND_LIKES_FOR_FILM, filmId);
    }


    public void insertFilmAndDirector(int id, Set<Director> directors) {

        List<Integer> directorIds = directors.stream().map(Director::getId).toList();
        log.info("Добавление в таблицу film_directors {}", directorIds);
        for (int directorId : directorIds) {
            update(INSERT_FILM_DIRECTOR, id, directorId);
        }
    }

    public Film createFilm(Film film) {
        int generatedId = insert(INSERT_FILM_VALUES, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        film.setId(generatedId);
        insertFilmAndGenre(generatedId, film.getGenres());
        insertFilmAndDirector(generatedId, film.getDirectors());
        return film;
    }

    public Film updateFilm(Film film) {
        isRealFilmId(List.of(film.getId()));
        update(UPDATE_FILM_BY_ID, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa()
                        .getId(), film.getId());
        log.info("Updste dir {} ", film.getDirectors());
        insertFilmAndDirector(film.getId(), film.getDirectors());
        return film;
    }

    public Film getFilm(int id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));

    }

    public List<Integer> getIdsForGenres(int id) {
        return findManyIds(GET_GENRES_BY_FILM, id);
    }


    public List<Film> getAllFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    public List<Film> getTopRatedFilms(int count, Integer genreId, Integer year) {
        return findMany(FIND_TOP_POPULAR_QUERY, genreId, genreId, year, year, count);
    }

    public List<Film> idToFilmConverter(List<Integer> ids) {
        List<Film> films = new ArrayList<>();
        for (int filmId : ids) {
            films.add(findOne(FIND_BY_ID_QUERY, filmId)
                    .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден")));
        }
        return films;
    }


    public void isRealFilmId(List<Integer> filmIds) {
        List<Film> films = findMany(FIND_BY_ID_QUERY, filmIds.toArray());

        if (films.size() != filmIds.size()) {
            for (int i = 0; i < filmIds.size(); i++) {
                if (i >= films.size() || films.get(i) == null) {
                    throw new NotFoundException("Фильм с ID " + filmIds.get(i) + " не найден");
                }
            }
        }
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return findMany(GET_COMMON_FILMS, userId, friendId);
    }
}