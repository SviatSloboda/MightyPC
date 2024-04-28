package de.mightypc.backend.exception.pc.hardware;

import java.util.NoSuchElementException;

public class HddNotFoundException extends NoSuchElementException {
    public HddNotFoundException(String s) {
        super(s);
    }
}
