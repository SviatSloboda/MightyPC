package de.mightypc.backend.model.pc.specs;

import java.math.BigDecimal;

public record HardwareSpec(
        String name,
        String description,
        BigDecimal price,
        float rating
) {

}
