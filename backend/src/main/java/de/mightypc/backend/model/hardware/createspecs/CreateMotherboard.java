package de.mightypc.backend.model.hardware.createspecs;

import de.mightypc.backend.model.hardware.HardwareSpec;

public record CreateMotherboard(
        HardwareSpec hardwareSpec,
        int energyConsumption,
        String socket
) {
}
