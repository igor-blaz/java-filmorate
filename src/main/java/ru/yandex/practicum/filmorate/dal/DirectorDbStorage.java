package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Slf4j
@Repository
public class DirectorDbStorage extends BaseRepository<Director> {
    private static final String FIND_ALL_DIR_QUERY = "SELECT * FROM directors;";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE id = ?;";
    private static final String FIND_DIRECTOR_QUERY = "SELECT * FROM directors WHERE id = ?;";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO directors " +
            "(director_name) VALUES (?);";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE directors SET director_name = ? " +
            "WHERE director_id = ?;";


    public DirectorDbStorage(JdbcTemplate jdbcTemplate, DirectorRowMapper mapper) {
        super(jdbcTemplate, mapper);
    }

    public List<Director> findAllDirectors() {
        return findMany(FIND_ALL_DIR_QUERY);
    }

    public Director findDirectorById(int id) {
        return findOne(FIND_DIRECTOR_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Режиссер с ID " + id + " не найден"));
    }

    public Director insertDirector(Director director) {
        director.setId(insert(INSERT_DIRECTOR_QUERY, director.getName()));
        return director;
    }

    public Director updateDirector(Director director) {
        isRealDirectorId(List.of(director.getId()));
        update(UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
        return director;
    }

    public void deleteDirector(int id) {
        isRealDirectorId(List.of(id));
        update(DELETE_DIRECTOR_QUERY, id);
    }

    public void isRealDirectorId(List<Integer> directorIds) {
        List<Director> directors = findMany(FIND_DIRECTOR_QUERY, directorIds.toArray());

        if (directors.size() != directorIds.size()) {
            for (int i = 0; i < directorIds.size(); i++) {
                if (i >= directors.size() || directors.get(i) == null) {
                    throw new NotFoundException("Режиссер с ID " + directorIds.get(i) + " не найден");
                }
            }
        }
    }


}
