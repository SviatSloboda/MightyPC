package de.mightypc.backend.model.specs.createspecs;

import java.math.BigDecimal;

public record CreateRam(
        String name,
        String description,
        String type,
        int energyConsumption,
        int memorySize,
        BigDecimal price,
        float rating
) {
}
