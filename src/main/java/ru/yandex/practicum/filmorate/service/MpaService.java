package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;
    private final MpaMapper mpaMapper;

    @Autowired
    public MpaService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage, MpaMapper mpaMapper) {
        this.mpaStorage = mpaStorage;
        this.mpaMapper = mpaMapper;
    }

    public Collection<MpaDto> findAllMpa() {
        return mpaStorage.findAllMpa().stream()
                .map(mpaMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<MpaDto> findById(long mpaId) {
        return mpaStorage.findById(mpaId)
                .map(mpaMapper::toDto);
    }
}
