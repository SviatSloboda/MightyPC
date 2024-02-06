package de.mightypc.backend.model;

import de.mightypc.backend.model.specs.PcCase;

public record CreatePC(
        String name,
        Specs specs,
        PcCase pcCase
) {
}
