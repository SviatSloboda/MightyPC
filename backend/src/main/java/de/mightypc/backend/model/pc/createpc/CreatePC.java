package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.SpecsIds;

public record CreatePC(
        HardwareSpec hardwareSpec,
        SpecsIds specsIds
) {
}
