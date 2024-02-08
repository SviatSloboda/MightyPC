package de.mightypc.backend.exception;

public class HardwareNotFoundException extends RuntimeException{
    public HardwareNotFoundException(String message){
        super(message);
    }
    public HardwareNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
