package de.mightypc.backend.exception.pc;

import java.util.NoSuchElementException;

public class WorkstationNotFoundException extends NoSuchElementException {
    public WorkstationNotFoundException(String s) {
        super(s);
    }
}
