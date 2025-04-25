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
            SELECT film_id
            FROM film_likes
            GROUP BY film_id
            ORDER BY COUNT(user_id) DESC
            LIMIT ?
            """;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String INSERT_FILM_VALUES = "INSERT INTO film " +
            "(name, description, release_date, duration, mpa_id) Values(?,?,?,?,?);";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genre (film_id, genre_id) Values(?,?);";
    private static final String INSERT_FILM_DIRECTOR = "INSERT INTO film_directors (film_id, director_id) Values(?,?);";
    private static final String ADD_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_GENRES_BY_FILM = "SELECT genre_id FROM film_genre WHERE " +
            "film_id = ?  ORDER BY genre_id";
   

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

    public List<Integer> findPopularFromArray(List<Integer> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyList();
        }
        String params = paramsMaker(filmIds.size());
        String FIND_POPULAR_FROM_ARRAY =
                "SELECT film_id, COUNT(user_id) AS like_count " +
                        "FROM film_likes " +
                        "WHERE film_id IN (" + params + ")" +
                        "GROUP BY film_id " +
                        "ORDER BY like_count DESC ";
        return findManyIds(FIND_POPULAR_FROM_ARRAY, filmIds);
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

    public void insertFilmAndDirector(int id, Set<Director> directors) {
        List<Integer> directorIds = directors.stream().map(Director::getId).toList();
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

    public List<Film> getTopRatedFilms(int count) {
        return idToFilmConverter(findManyIds(FIND_TOP_POPULAR_QUERY, count));
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
}
