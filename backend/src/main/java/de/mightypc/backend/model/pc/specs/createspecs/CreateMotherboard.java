package de.mightypc.backend.model.pc.specs.createspecs;

import de.mightypc.backend.model.pc.specs.HardwareSpec;

public record CreateMotherboard(
        HardwareSpec hardwareSpec,
        int energyConsumption,
        String socket
) {
}

