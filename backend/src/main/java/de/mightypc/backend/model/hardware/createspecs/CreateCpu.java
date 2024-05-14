package de.mightypc.backend.model.hardware.createspecs;

import de.mightypc.backend.model.hardware.HardwareSpec;

public record CreateCpu(
        HardwareSpec hardwareSpec,
        int energyConsumption,
        String socket
) {
}
