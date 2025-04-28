package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorDbStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

    @Autowired
    public DirectorService(DirectorDbStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }


    public Director getDirectorById(int id) {
        return directorDbStorage.findDirectorById(id);
    }

    public Director addDirector(Director director) {
        return directorDbStorage.insertDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorDbStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        directorDbStorage.deleteDirector(id);
    }

    public List<Director> getAllDirectors() {
        return directorDbStorage.findAllDirectors();
    }


}
