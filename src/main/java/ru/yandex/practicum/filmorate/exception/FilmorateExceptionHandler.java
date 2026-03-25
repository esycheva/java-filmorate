package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FilmorateExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Произошла ошибка: " + ex.getMessage());
    }

    @ExceptionHandler(RecordNotValidException.class)
    public ResponseEntity<String> handleIllegalArgumentException(RecordNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("{}");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ошибка: " + ex.getMessage());
    }
}




