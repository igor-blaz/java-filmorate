package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String UPDATE_FILM_BY_ID = "UPDATE films SET name = ?, " +
            "description = ?, release_date=?, duration = ?, mpa_id=?, genres_id=? " +
            "WHERE id =?;";
    private static final String FIND_TOP_POPULAR_QUERY = """
            SELECT film_id, COUNT(user_id) AS like_count
            FROM film_likes
            GROUP BY film_id
            ORDER BY like_count DESC
            LIMIT ?;
            """;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String DELETE_BY_FILM_ID_QUERY = "DELETE FROM film WHERE id = ?;";
    private static final String INSERT_FILM_VALUES = "INSERT INTO film " +
            "(name, description, release_date, duration) Values(?,?,?,?);";
    private static final String ADD_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKE_COUNT_QUERY = "SELECT COUNT(*) FROM film_likes WHERE film_id = ?";
    private static final String GET_FILMS_BY_GENRE = "SELECT * FROM film_genre WHERE genre_id = ?";
    MpaDbStorage mpaDbStorage;
    GenreDbStorage genreDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper mapper, MpaDbStorage mpaDbStorage,
                         GenreDbStorage genreDbStorage) {
        super(jdbcTemplate, mapper);
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    public void makeLike(int filmId, int userId) {
        update(ADD_LIKE_QUERY, filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    public Integer getLikesCount(int filmId) {
        return jdbc.queryForObject(GET_LIKE_COUNT_QUERY, Integer.class, filmId);
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Создание фильма...");
        int generatedId = insert(INSERT_FILM_VALUES, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
        log.info("Фильм создан с ID {}", generatedId);
        log.info(String.valueOf(film.getMpa()));
        System.out.println(film.getMpa());
        film.setId(generatedId);
        film.setMpa(findNameForMpa(film.getMpa()));
        film.setGenres(genreDbStorage.getManyGenres(film.getGenres()));

        System.out.println(film.getGenres());
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        findOne(DELETE_BY_FILM_ID_QUERY, film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + film.getId() + " не найден"));
    }

    @Override
    public Film updateFilm(Film film) {
        update(UPDATE_FILM_BY_ID, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
        return film;
    }

    public Film getFilm(int id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }

    public List<Film> getAllFilms() {
        System.out.println(findMany(FIND_ALL_QUERY));
        return findMany(FIND_ALL_QUERY);
    }

    public List<Film> getTopRatedFilms(int count) {
        if (getAllFilms().size() < count) {
            return getAllFilms();
        }
        return findMany(FIND_TOP_POPULAR_QUERY, count);
    }

    public List<Film> getFilmByGenre(int genreId) {
        List<Integer> ids = findManyIds(GET_FILMS_BY_GENRE, genreId);
        return idToFilmConverter(ids);
    }

    public List<Film> idToFilmConverter(List<Integer> ids) {
        List<Film> films = new ArrayList<>();
        for (int filmId : ids) {
            films.add(findOne(FIND_BY_ID_QUERY, filmId).
                    orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден")));
        }
        return films;
    }

    public Mpa findNameForMpa(Mpa mpa) {
        return mpaDbStorage.findById(mpa.getId());
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
