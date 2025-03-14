package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> filmMap = new HashMap<>();
    private Integer id = 1;
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final TreeSet<Film> rated = new TreeSet<>(Comparator.comparing(Film::getLikesSize));

    @Override
    public Film createFilm(Film film) {
        film.setId(id++);
        filmMap.put(film.getId(), film);
        rated.add(film);
        log.info("Фильм добавлен: id={}", film.getId());
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        filmMap.remove(film.getId());
        rated.remove(film);
    }

    @Override
    public Film updateFilm(Film film) {
        isRealFilmId(List.of(film.getId()));
        filmMap.put(film.getId(), film);
        rated.add(film);
        log.info("Фильм обновлен");
        return film;
    }

    public Film getFilm(int id) {
        isRealFilmId(List.of(id));
        return filmMap.get(id);
    }

    public List<Film> getAllFilms() {
        return filmMap.values().stream().toList();
    }

    public List<Film> getRatedFilms() {
        return rated.stream().toList();
    }

    public void isRealFilmId(List<Integer> idS) {
        for (int id : idS) {
            if (!filmMap.containsKey(id)) {
                throw new NotFoundException("Аккаунт с ID " + id + " не найден");
            }
        }
    }

}
