package de.mightypc.backend.model.pc.specs.createspecs;

import de.mightypc.backend.model.pc.specs.HardwareSpec;

public record CreateCpu(
        HardwareSpec hardwareSpec,
        int performance,
        int energyConsumption
) {
}
