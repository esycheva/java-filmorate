package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend {
    private Long id;
    private Long userId;
    private Long friendId;
    private boolean approved;
}
