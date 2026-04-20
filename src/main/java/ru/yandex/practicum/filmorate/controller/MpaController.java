package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService service;

    private static final Logger log = LoggerFactory.getLogger(MpaController.class);

    @GetMapping
    public Collection<MpaDto> findAllMpa() {
        return service.findAllMpa();
    }

    @GetMapping("/{mpaId}")
    public Optional<MpaDto> findById(@PathVariable long mpaId) {
        return service.findById(mpaId);
    }
}
