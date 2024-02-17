package de.mightypc.backend.model.specs.createspecs;

import de.mightypc.backend.model.specs.HardwareSpec;

public record CreateMotherboard(
        HardwareSpec hardwareSpec,
        int energyConsumption,
        String[] graphicCardCompatibility,
        String[] processorCompatibility
) {
}

