package de.mightypc.backend.model.hardware.createspecs;

import de.mightypc.backend.model.hardware.HardwareSpec;

public record CreatePcCase(
        HardwareSpec hardwareSpec,
        String dimensions
) {
}
