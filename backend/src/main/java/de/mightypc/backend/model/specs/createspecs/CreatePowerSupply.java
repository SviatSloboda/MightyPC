package de.mightypc.backend.model.specs.createspecs;

import java.math.BigDecimal;

public record CreatePowerSupply(
        String name,
        String description,
        int power,
        BigDecimal price,
        float rating
) {
}
