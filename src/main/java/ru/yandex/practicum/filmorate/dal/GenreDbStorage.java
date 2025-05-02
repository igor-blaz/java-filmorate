package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genre;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?;";
    private static final String REMOVE_GENRES_BY_FILM_ID = "DELETE FROM film_genre WHERE film_id=?";
    private static final String INSERT_FILM_GENRE = "INSERT INTO film_genre " +
            "(film_id, genre_id) VALUES (?, ?);";
    private static final String FIND_GENRE_BY_FILM_QUERY = "SELECT g.id, g.name FROM film_genre fg " +
            "JOIN genre g ON g.id = fg.genre_id WHERE fg.film_id = ?";

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        super(jdbcTemplate, genreRowMapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre getGenre(int id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Жанр с ID " + id + " не найден"));
    }

    public Set<Genre> getManyGenres(Set<Genre> genresWithoutName) {
        Set<Genre> genres = new HashSet<>();
        for (Genre genre : genresWithoutName) {
            int id = genre.getId();
            genres.add(getGenre(id));
        }
        return genres;
    }

    public void deleteGenresOnFilm(int filmId) {
        update(REMOVE_GENRES_BY_FILM_ID, filmId);
    }

    public void insertManyGenres(int filmId, Set<Genre> directors) {
        for (Genre genre : directors) {
            addGenreToFilm(filmId, genre.getId());
        }
    }

    public void addGenreToFilm(int filmId, int genreId) {
        update(INSERT_FILM_GENRE, filmId, genreId);
    }

    public Set<Genre> findGenresByFilmId(int filmId) {
        return new HashSet<>(findMany(FIND_GENRE_BY_FILM_QUERY, filmId));
    }
}
