package de.mightypc.backend.model.configurator;

import java.math.BigDecimal;

public record ItemForConfigurator(
        String id,
        String name,
        BigDecimal price,
        String image,
        String pathNameForItemDetailsPage
) {
}
