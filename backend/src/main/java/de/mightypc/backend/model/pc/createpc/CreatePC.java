package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.SpecsIds;

public record CreatePC(
        HardwareSpec hardwareSpec,
        SpecsIds specsIds
) {
}
