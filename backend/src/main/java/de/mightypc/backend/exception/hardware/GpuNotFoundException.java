package de.mightypc.backend.exception.pc.hardware;

import java.util.NoSuchElementException;

public class GpuNotFoundException extends NoSuchElementException {
    public GpuNotFoundException(String s) {
        super(s);
    }
}
