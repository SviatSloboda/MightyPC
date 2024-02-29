package de.mightypc.backend.exception.pc;

import de.mightypc.backend.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@ControllerAdvice
public class PCExceptionHandler {
    @ExceptionHandler(value = {HardwareNotFoundException.class})
    public ResponseEntity<Object> handleHardwareNotFoundException(HardwareNotFoundException hardwareNotFoundException) {
        ErrorResponse hardwareException = new ErrorResponse(
                hardwareNotFoundException.getMessage(),
                hardwareNotFoundException.getCause(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now()
        );

        return new ResponseEntity<>(hardwareException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {PcNotFoundException.class})
    public ResponseEntity<Object> handlePcNotFoundException(PcNotFoundException pcNotFoundException) {
        ErrorResponse pcException = new ErrorResponse(pcNotFoundException.getMessage(),
                pcNotFoundException.getCause(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now()
        );

        return new ResponseEntity<>(pcException, HttpStatus.NOT_FOUND);
    }
}
