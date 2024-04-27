package de.mightypc.backend.exception.pc;

import java.util.NoSuchElementException;

public class PcNotFoundException extends NoSuchElementException {
    public PcNotFoundException(String s) {
        super(s);
    }
}
