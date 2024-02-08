package de.mightypc.backend.model.specs.createspecs;

import java.math.BigDecimal;

public record CreateCpu(
        String name,
        String description,
        BigDecimal price,
        int performance,
        int energyConsumption,
        float rating
) {
}
