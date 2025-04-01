package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM films WHERE email = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String DELETE_BY_FILM_ID_QUERY = "DELETE FROM films WHERE id = ?;";



    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper mapper) {
        super(jdbcTemplate, mapper);

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
        findOne(DELETE_BY_FILM_ID_QUERY, film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }

    public Film getFilm(int id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
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
