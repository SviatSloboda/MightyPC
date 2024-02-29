package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.pc.specs.HardwareSpec;

public record CreatePC(
        CreateSpecs createSpecs,
        HardwareSpec hardwareSpec
) {
}
