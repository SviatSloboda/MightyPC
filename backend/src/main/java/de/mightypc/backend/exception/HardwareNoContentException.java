package de.mightypc.backend.exception;

public class HardwareNoContentException extends RuntimeException{
    public HardwareNoContentException(String message) {
        super(message);
    }

    public HardwareNoContentException(String message, Throwable cause) {
        super(message, cause);
    }
}
