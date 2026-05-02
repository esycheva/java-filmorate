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
FROM films AS f
WHERE f.id IN (
	SELECT fl.film_id 
	FROM FILM_LIKES AS fl
	GROUP BY (fl.film_id)
	ORDER BY COUNT(fl.film_id) DESC
) LIMIT 5;
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
Получение друзей (для пользователя с идентификатором 1):
```SQL
SELECT fu.name
FROM users AS u
INNER JOIN friends AS f ON u.id = f.user_id
INNER JOIN users AS fu ON f.friend_id = fu.id
WHERE u.id = 1;
```
Получение общих друзей для пользователя с идентификатором 1 и идентификатором 2

```SQL
SELECT *
FROM friends AS fr
INNER JOIN users AS u ON fr.friend_id = u.id
WHERE fr.friend_id IN (
    SELECT f.friend_id AS id
    FROM friends AS f
    WHERE f.user_id = 1
) AND fr.user_id = 2;
```

