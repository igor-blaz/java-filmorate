package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MpaDbStorage extends BaseRepository<Mpa> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?;";
    private static final String INSERT_MPA_VALUES = "INSERT INTO Mpa (name) Values(?);";
    private static final String UPDATE_MPA_VALUES_BY_ID = "UPDATE mpa SET name = ? WHERE id = ?;";
    private static final String DELETE_MPA_VALUES_BY_ID = "DELETE FROM mpa WHERE id = ?;";

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper mpaRowMapper) {
        super(jdbcTemplate, mpaRowMapper);
    }

    public Mpa insertMpa(Mpa mpa) {
        int id = insert(INSERT_MPA_VALUES, mpa.getName());
        return new Mpa(id, mpa.getName());
    }

    public int updateMpa(Mpa mpa) {
        return update(UPDATE_MPA_VALUES_BY_ID, mpa.getName(), mpa.getId());
    }

    public int deleteMpa(Mpa mpa) {
        return update(DELETE_MPA_VALUES_BY_ID, mpa.getId());
    }

    public List<Mpa> getAllMpa() {
        return findMany(FIND_ALL_QUERY);
    }

    public Mpa findById(int id) {
        log.info("SQL запрос поиск Mpa {}", id);
        return findOne(FIND_BY_ID_QUERY, id).
                orElseThrow(() -> new NotFoundException("Рейтинг с ID " + id + " не найден"));
    }


}

