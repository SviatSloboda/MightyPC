package de.mightypc.backend.model.pc;

import de.mightypc.backend.model.pc.specs.*;
import de.mightypc.backend.model.specs.*;

import java.math.BigDecimal;

public record Specs(
        CPU cpu,
        GPU gpu,
        Motherboard motherboard,
        RAM ram,
        SSD ssd,
        HDD hdd,
        PowerSupply powerSupply,
        BigDecimal totalPrice,
        int totalPerformance
) {
}
