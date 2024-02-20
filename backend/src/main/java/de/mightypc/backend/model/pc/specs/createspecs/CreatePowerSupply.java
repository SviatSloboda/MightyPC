package de.mightypc.backend.model.pc.specs.createspecs;

import de.mightypc.backend.model.pc.specs.HardwareSpec;

public record CreatePowerSupply(
        HardwareSpec hardwareSpec,
        int power
) {
}
