package de.mightypc.backend.exception.pc.hardware;

import java.util.NoSuchElementException;

public class RamNotFoundException extends NoSuchElementException {
    public RamNotFoundException(String s) {
        super(s);
    }
}
