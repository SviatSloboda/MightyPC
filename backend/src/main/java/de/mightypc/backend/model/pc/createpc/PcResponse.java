package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.pc.specs.HardwareSpec;
import de.mightypc.backend.model.pc.specs.SpecsIds;
import de.mightypc.backend.model.pc.specs.SpecsNames;
import org.springframework.data.annotation.Id;

import java.util.List;

public record PcResponse(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        SpecsIds specsIds,
        SpecsNames specsNames,
        int energyConsumption,
        List<String> photos
) {
}
