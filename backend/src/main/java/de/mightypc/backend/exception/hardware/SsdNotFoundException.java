package de.mightypc.backend.exception.pc.hardware;

import java.util.NoSuchElementException;

public class SsdNotFoundException extends NoSuchElementException {
    public SsdNotFoundException(String s) {
        super(s);
    }
}
