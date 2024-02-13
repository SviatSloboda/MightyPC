package de.mightypc.backend.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public record ErrorResponse(
        String message,
        Throwable throwable,
        HttpStatus httpStatus,
        ZonedDateTime timeStamp
) {
}
