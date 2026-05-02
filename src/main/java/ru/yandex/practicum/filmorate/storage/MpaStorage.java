package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Component
public interface MpaStorage {

    public List<Mpa> findAllMpa();

    public Optional<Mpa> findById(Long mpaId);
}
