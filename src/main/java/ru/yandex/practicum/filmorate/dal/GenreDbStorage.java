package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genre;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?;";
    private static final String INSERT_GENRE_VALUES = "INSERT INTO genre (name) Values(?);";
    private static final String UPDATE_GENRE_VALUES_BY_ID = "UPDATE genre SET name = ? WHERE id = ?;";
    private static final String DELETE_GENRE_VALUES_BY_ID = "DELETE FROM genre WHERE id = ?;";

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper) {
        super(jdbcTemplate, genreRowMapper);
    }

    public Genre insertGenre(Genre genre) {
        int id = insert(INSERT_GENRE_VALUES, genre.getName());
        return new Genre(id, genre.getName());
    }

    public int updateGenre(Genre genre) {
        return update(UPDATE_GENRE_VALUES_BY_ID, genre.getName(), genre.getId());
    }

    public int deleteGenre(Genre genre) {
        return update(DELETE_GENRE_VALUES_BY_ID, genre.getId());
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre getGenre(int id) {
        return findOne(FIND_BY_ID_QUERY, id).
              orElseThrow(() -> new NotFoundException("Жанр с ID " + id + " не найден"));
    }
}
