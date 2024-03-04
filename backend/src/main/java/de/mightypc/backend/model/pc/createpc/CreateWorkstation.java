package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.SpecsIds;

public record CreateWorkstation(
        HardwareSpec hardwareSpec,
        SpecsIds specsIds,
        int cpuNumber,
        int gpuNumber
) {
}
