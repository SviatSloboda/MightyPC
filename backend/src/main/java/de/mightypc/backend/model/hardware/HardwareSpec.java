package de.mightypc.backend.model.hardware;

import java.math.BigDecimal;

public record HardwareSpec(
        String name,
        String description,
        BigDecimal price,
        float rating
) {
}
