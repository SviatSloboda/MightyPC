package de.mightypc.backend.model.pc.specs.createspecs;

import de.mightypc.backend.model.pc.specs.HardwareSpec;

public record CreateSsd(
        HardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption
) {
}
