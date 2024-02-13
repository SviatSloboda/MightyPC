package de.mightypc.backend.model.specs.createspecs;

import de.mightypc.backend.model.specs.HardwareSpec;

public record CreateCpu(
        HardwareSpec hardwareSpec,
        int performance,
        int energyConsumption
) {
}
