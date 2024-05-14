package de.mightypc.backend.model.pc.createpc;

import de.mightypc.backend.model.hardware.HardwareSpec;
import de.mightypc.backend.model.hardware.SpecsIds;
import de.mightypc.backend.model.hardware.SpecsNames;
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
