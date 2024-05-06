package de.mightypc.backend.exception.hardware;

import java.util.NoSuchElementException;

public class PowerSupplyNotFoundException extends NoSuchElementException {
    public PowerSupplyNotFoundException(String s) {
        super(s);
    }
}
