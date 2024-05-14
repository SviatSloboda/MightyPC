package de.mightypc.backend.exception.pc;

import java.util.NoSuchElementException;

public class UserPcNotFoundException extends NoSuchElementException {
    public UserPcNotFoundException(String s) {
        super(s);
    }
}
