package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Component
public interface GenreStorage {

    public List<Genre> findAllGenres();

    public Optional<Genre> findById(Long genreId);
}
