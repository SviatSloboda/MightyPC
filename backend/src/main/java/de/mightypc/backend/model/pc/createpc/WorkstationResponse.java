package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.SpecsIds;
import de.mightypc.backend.model.pc.specs.SpecsNames;
import org.springframework.data.annotation.Id;

import java.util.List;

public record WorkstationResponse(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        SpecsIds specsIds,
        SpecsNames specsNames,
        int cpuNumber,
        int gpuNumber,
        int energyConsumption,
        List<String> photos
) {
}