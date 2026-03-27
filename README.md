# java-filmorate

Бэкенд для сервиса, который работает с фильмами и оценками пользователей,
а также возвращает топ-5 фильмов, рекомендованных к просмотру.

[Схема БД](filmorate_db.png)

#### Примеры запросов (фильмы)

Получение всех фильмов

```SQL
SELECT * FROM films
```

Получение фильма по идентификатору (например, 1)

```SQL
SELECT * FROM film WHERE id = 1
```
Получение 5 популярных фильмов

```SQL

SELECT * 
FROM fimls WHERE id IN (
    SELECT l.film_id
    FROM films AS f
    INNER JOIN likes AS l ON f.id = l.film_id
    GROUP BY l.film_id
    ORDER BY COUNT(l.film_id) DESC
    LIMIT 5
);
```

#### Примеры запросов (пользователи)

Получение всех пользователей

```SQL
SELECT * FROM users
```

Получение пользователя по идентификатору (например, 2)

```SQL
SELECT * FROM users WHERE id = 2
```
Получение друзей
```SQL
SELECT fu.name
FROM users AS u
INNER JOIN friends AS f ON u.id = f.user_id
INNER JOIN users AS fu ON f.friend_id = u.id
```
Получение общих друзей для пользователя с идентификатором 1
```SQL
SELECT * 
FROM users AS us
WHERE us.id IN (
    SELECT f.friend_id
    FROM friends AS f
    INNER JOIN users AS u ON f.user_id = u.id
    WHERE f.user_id = 1
) AND us.id <> 1;
```

