package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.SpecsIds;

public record CreateWorkstation(
        HardwareSpec hardwareSpec,
        SpecsIds specsIds,
        int cpuNumber,
        int gpuNumber
) {
}
