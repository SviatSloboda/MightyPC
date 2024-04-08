package de.mightypc.backend.model.hardware.createspecs;

import de.mightypc.backend.model.hardware.HardwareSpec;

public record CreateSsd(
        HardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption
) {
}
