package de.mightypc.backend.model.hardware.createspecs;

import de.mightypc.backend.model.hardware.HardwareSpec;

public record CreatePowerSupply(
        HardwareSpec hardwareSpec,
        int power
) {
}
