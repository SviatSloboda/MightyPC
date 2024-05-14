package de.mightypc.backend.exception.shop;

import java.util.NoSuchElementException;

public class ItemNotFoundException extends NoSuchElementException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
