package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Slf4j
@Repository
public class MpaDbStorage extends BaseRepository<Mpa> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?;";
    private static final String INSERT_MPA_VALUES = "INSERT INTO Mpa (name) Values(?);";

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper mpaRowMapper) {
        super(jdbcTemplate, mpaRowMapper);
    }

    public Mpa insertMpa(Mpa mpa) {
        int id = insert(INSERT_MPA_VALUES, mpa.getName());
        return new Mpa(id, mpa.getName());
    }

    public List<Mpa> getAllMpa() {
        return findMany(FIND_ALL_QUERY);
    }

    public Mpa findById(int id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с ID " + id + " не найден"));
    }


}

