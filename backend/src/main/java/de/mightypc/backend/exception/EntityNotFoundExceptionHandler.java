package de.mightypc.backend.exception;

import de.mightypc.backend.exception.pc.PcNotFoundException;
import de.mightypc.backend.exception.pc.UserPcNotFoundException;
import de.mightypc.backend.exception.pc.WorkstationNotFoundException;
import de.mightypc.backend.exception.pc.hardware.*;
import de.mightypc.backend.exception.shop.ItemNotFoundException;
import de.mightypc.backend.exception.shop.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class EntityNotFoundExceptionHandler {

    @ExceptionHandler({
            CpuNotFoundException.class,
            GpuNotFoundException.class,
            MotherboardNotFoundException.class,
            RamNotFoundException.class,
            SsdNotFoundException.class,
            HddNotFoundException.class,
            PcCaseNotFoundException.class,
            PowerSupplyNotFoundException.class,
            PcNotFoundException.class,
            UserPcNotFoundException.class,
            WorkstationNotFoundException.class,
            UserPcNotFoundException.class,
            ItemNotFoundException.class,
            OrderNotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(NoSuchElementException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}