package de.mightypc.backend.model.specs.createspecs;

import java.math.BigDecimal;

public record CreateHdd(
        String name,
        String description,
        int capacity,
        int energyConsumption,
        BigDecimal price,
        float rating
) {
}
