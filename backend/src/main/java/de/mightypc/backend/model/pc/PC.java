package de.mightypc.backend.model.pc;

import de.mightypc.backend.model.pc.specs.Specs;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record PC(
        @Id String id,
        HardwareSpec hardwareSpec,
        Specs specs,
        int energyConsumption,
        List<String> photos
) {
    public PC(String id, HardwareSpec hardwareSpec, Specs specs, int energyConsumption) {
        this(id, hardwareSpec, specs, energyConsumption, new ArrayList<>(Collections.emptyList()));
    }

    public PC(HardwareSpec hardwareSpec, Specs specs, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, specs, energyConsumption);
    }

    public PC withPhotos(List<String> allPhotos) {
        return new PC(id(), hardwareSpec(), specs(), energyConsumption(), allPhotos);
    }
}
