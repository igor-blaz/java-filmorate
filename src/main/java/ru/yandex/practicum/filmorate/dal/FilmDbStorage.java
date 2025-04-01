package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper mapper;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper mapper) {
        super(jdbcTemplate, mapper);
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate())); // Убедись, что у тебя releaseDate — это LocalDate
            ps.setInt(4, film.getDuration());
            return ps;
        }, keyHolder);

        // Устанавливаем ID, который был сгенерирован БД
        if (keyHolder.getKey() != null) {
            film.setId(keyHolder.getKey().intValue());
        }

        return film;
    }


    @Override
    public void deleteFilm(Film film) {
     //   jdbcTemplate.update(query, id);
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    public Film getFilm(int id) {
        String query = "SELECT * FROM films WHERE ID = ?";
        return findOne(query, mapper, id);
    }
}
