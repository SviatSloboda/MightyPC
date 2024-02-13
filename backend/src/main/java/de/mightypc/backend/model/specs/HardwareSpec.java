package de.mightypc.backend.model.specs;

import java.math.BigDecimal;

public record HardwareSpec(
        String name,
        String description,
        BigDecimal price,
        float rating
) {

}
