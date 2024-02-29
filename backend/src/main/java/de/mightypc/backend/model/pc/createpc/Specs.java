package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.pc.specs.*;

public record Specs(
        CPU cpu,
        GPU gpu,
        Motherboard motherboard,
        RAM ram,
        SSD ssd,
        HDD hdd,
        PowerSupply powerSupply,
        PcCase pcCase
) {
}
