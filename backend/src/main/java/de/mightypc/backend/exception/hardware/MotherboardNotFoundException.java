package de.mightypc.backend.exception.hardware;

import java.util.NoSuchElementException;

public class MotherboardNotFoundException extends NoSuchElementException {
    public MotherboardNotFoundException(String s) {
        super(s);
    }
}
