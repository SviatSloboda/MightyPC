package de.mightypc.backend.model.hardware.createspecs;

import de.mightypc.backend.model.hardware.HardwareSpec;

public record CreateRam(
        HardwareSpec hardwareSpec,
        String type,
        int energyConsumption,
        int memorySize
) {
}
