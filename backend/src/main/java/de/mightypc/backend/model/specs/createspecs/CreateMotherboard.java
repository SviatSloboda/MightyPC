package de.mightypc.backend.model.specs.createspecs;

import de.mightypc.backend.model.specs.CPU;
import de.mightypc.backend.model.specs.GPU;

public record CreateMotherboard(
        String name,
        String description,
        int price,
        int energyConsumption,
        GPU[] graphicCardCompatibility,
        CPU[] processorCompatibility,
        float rating
) {
}
