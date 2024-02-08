package de.mightypc.backend.model.specs.createspecs;

import java.math.BigDecimal;

public record CreateSsd(
        String name,
        String description,
        int energyConsumption,
        BigDecimal price,
        float rating
) {
}
