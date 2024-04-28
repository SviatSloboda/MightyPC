package de.mightypc.backend.exception.pc.hardware;

import java.util.NoSuchElementException;

public class PowerSupplyNotFoundException extends NoSuchElementException {
    public PowerSupplyNotFoundException(String s) {
        super(s);
    }
}
