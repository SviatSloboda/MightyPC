package de.mightypc.backend.model.specs.createspecs;

import de.mightypc.backend.model.specs.HardwareSpec;

public record CreateRam(
        HardwareSpec hardwareSpec,
        String type,
        int energyConsumption,
        int memorySize
) {
}
