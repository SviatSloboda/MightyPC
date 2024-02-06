package de.mightypc.backend.model;

import de.mightypc.backend.model.specs.*;

public record Specs(
        CPU cpu,
        GPU gpu,
        Motherboard motherboard,
        RAM ram,
        SSD ssd,
        HDD hdd,
        PowerSupply powerSupply,
        int totalPrice,
        int totalPerformance
) {
}
