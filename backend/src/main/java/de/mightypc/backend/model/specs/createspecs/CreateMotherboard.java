package de.mightypc.backend.model.specs.createspecs;

import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.model.specs.GPU;

import java.math.BigDecimal;

public record CreateMotherboard(
        String name,
        String description,
        BigDecimal price,
        int energyConsumption,
        GPU[] graphicCardCompatibility,
        CPU[] processorCompatibility,
        float rating
) {
}
