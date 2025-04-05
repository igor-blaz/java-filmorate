package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
    public class MpaService {
        private final MpaDbStorage mpaDbStorage;

        @Autowired
        public MpaService(MpaDbStorage mpaDbStorage) {
            this.mpaDbStorage = mpaDbStorage;
        }
        public Mpa getMpa(int id){
            return mpaDbStorage.findById(id);
        }
        public Mpa addMpa(Mpa mpa){
            return mpaDbStorage.insertMpa(mpa);
        }
        public List<Mpa> getAllMpa(){
            return mpaDbStorage.getAllMpa();
        }
    }

