package de.mightypc.backend.model.specs.createspecs;

import de.mightypc.backend.model.specs.HardwareSpec;

public record CreatePowerSupply(
        HardwareSpec hardwareSpec,
        int power
) {
}
