package de.mightypc.backend.model.pc.specs.createspecs;

import de.mightypc.backend.model.pc.specs.HardwareSpec;

public record CreateHdd(
        HardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption
) {
}
