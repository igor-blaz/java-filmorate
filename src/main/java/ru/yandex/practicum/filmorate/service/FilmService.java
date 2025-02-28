package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FilmService {

    private final InMemoryFilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    private void makeLike() {

    }

    private void removeLike() {

    }

    private List<Film> topRatedFilms() {
        Map<Integer, Film> films = InMemoryFilmStorage.getFilmMap();
    }
}
