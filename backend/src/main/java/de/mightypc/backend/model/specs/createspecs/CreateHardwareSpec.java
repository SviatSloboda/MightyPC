package de.mightypc.backend.model.specs.createspecs;

import java.math.BigDecimal;

public record CreateHardwareSpec(
        String name,
        String description,
        BigDecimal price,
        float rating
) {
}
