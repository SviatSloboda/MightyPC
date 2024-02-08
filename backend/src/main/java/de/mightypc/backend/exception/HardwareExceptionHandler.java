package de.mightypc.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class HardwareExceptionHandler {
    @ExceptionHandler(value = {HardwareNotFoundException.class})
    public ResponseEntity<Object> handleHardwareNotFoundException(HardwareNotFoundException hardwareNotFoundException){
        ErrorResponse hardwareException = new ErrorResponse(
                hardwareNotFoundException.getMessage(),
                hardwareNotFoundException.getCause(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now());

        return new ResponseEntity<>(hardwareException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {HardwareNoContentException.class})
    public ResponseEntity<Object> handleHardwareNoContentException(HardwareNoContentException hardwareNoContentException){
        ErrorResponse hardwareException = new ErrorResponse(
                hardwareNoContentException.getMessage(),
                hardwareNoContentException.getCause(),
                HttpStatus.NO_CONTENT,
                ZonedDateTime.now());

        return new ResponseEntity<>(hardwareException, HttpStatus.NO_CONTENT);
    }
}
