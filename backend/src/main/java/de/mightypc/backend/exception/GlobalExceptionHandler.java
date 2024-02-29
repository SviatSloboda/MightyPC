package de.mightypc.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;

@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleHardwareNotFoundException(Exception ex) {
        ErrorResponse globalException = new ErrorResponse(ex.getMessage(), ex.getCause(), HttpStatus.BAD_REQUEST, ZonedDateTime.now());

        return new ResponseEntity<>(globalException, HttpStatus.BAD_REQUEST);
    }
}
