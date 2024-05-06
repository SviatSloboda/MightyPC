package de.mightypc.backend.exception.hardware;

import java.util.NoSuchElementException;

public class GpuNotFoundException extends NoSuchElementException {
    public GpuNotFoundException(String s) {
        super(s);
    }
}
