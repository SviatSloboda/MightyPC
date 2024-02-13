package de.mightypc.backend.model.specs.createspecs;

import de.mightypc.backend.model.specs.HardwareSpec;

public record CreateSsd(
        HardwareSpec hardwareSpec,
        int energyConsumption
) {
}
