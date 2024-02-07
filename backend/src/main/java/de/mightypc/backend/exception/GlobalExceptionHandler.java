package de.mightypc.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ErrorMessage handleGlobalException(Exception ex) {
        return new ErrorMessage("The problem is erased: " + ex.getMessage());
    }
}