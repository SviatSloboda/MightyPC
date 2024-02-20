package de.mightypc.backend.model.pc.specs.createspecs;

import de.mightypc.backend.model.pc.specs.HardwareSpec;

public record CreateRam(
        HardwareSpec hardwareSpec,
        String type,
        int energyConsumption,
        int memorySize
) {
}
