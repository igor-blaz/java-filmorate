package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genre;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?;";

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
}
