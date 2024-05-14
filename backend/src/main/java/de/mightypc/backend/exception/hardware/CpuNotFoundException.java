package de.mightypc.backend.exception.hardware;

import java.util.NoSuchElementException;

public class CpuNotFoundException extends NoSuchElementException {
    public CpuNotFoundException(String s) {
        super(s);
    }
}
