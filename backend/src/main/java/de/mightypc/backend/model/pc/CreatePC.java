package de.mightypc.backend.model.pc;

import de.mightypc.backend.model.pc.specs.PcCase;

public record CreatePC(
        String name,
        Specs specs,
        PcCase pcCase
) {
}
