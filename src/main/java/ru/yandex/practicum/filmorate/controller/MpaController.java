package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private static final Logger log = LoggerFactory.getLogger(MpaController.class);
    private final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable int id) {
        log.info("Запрос на получение Mpa");
        return mpaService.getMpa(id);
    }

    @PostMapping
    public Mpa createMpa(@RequestBody Mpa mpa) {
        log.info("Добавление нового рейтинга");
        return mpaService.addMpa(mpa);
    }

    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaService.getAllMpa();
    }
}