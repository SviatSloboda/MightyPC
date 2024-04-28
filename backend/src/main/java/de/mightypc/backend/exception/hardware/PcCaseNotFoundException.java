package de.mightypc.backend.exception.pc.hardware;

import java.util.NoSuchElementException;

public class PcCaseNotFoundException extends NoSuchElementException {
    public PcCaseNotFoundException(String s) {
        super(s);
    }
}
