package de.mightypc.backend.exception.shop;

import de.mightypc.backend.exception.ErrorResponse;
import de.mightypc.backend.exception.shop.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class ShopExceptionHandler {
    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException hardwareNotFoundException) {
        ErrorResponse hardwareException = new ErrorResponse(
                hardwareNotFoundException.getMessage(),
                hardwareNotFoundException.getCause(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now());

        return new ResponseEntity<>(hardwareException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {OrderNotFoundException.class})
    public ResponseEntity<Object> handleOrderNotFoundException(OrderNotFoundException orderNotFoundException) {
        ErrorResponse orderException = new ErrorResponse(
                orderNotFoundException.getMessage(),
                orderNotFoundException.getCause(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now());

        return new ResponseEntity<>(orderException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ItemNotFoundException.class})
    public ResponseEntity<Object> handleItemNoFoundException(ItemNotFoundException itemNotFoundException) {
        ErrorResponse itemException = new ErrorResponse(
                itemNotFoundException.getMessage(),
                itemNotFoundException.getCause(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now());

        return new ResponseEntity<>(itemException, HttpStatus.NOT_FOUND);
    }
}
