package de.mightypc.backend.model.pc;

import de.mightypc.backend.model.pc.createpc.Specs;
import de.mightypc.backend.model.pc.specs.CPU;
import de.mightypc.backend.model.pc.specs.HardwareSpec;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record PC(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        Specs specs,
        List<String> photos
) {
    public PC(String id, HardwareSpec hardwareSpec, Specs specs) {
        this(id, hardwareSpec, specs, new ArrayList<>(Collections.emptyList()));
    }

    public PC(HardwareSpec hardwareSpec, Specs specs) {
        this(UUID.randomUUID().toString(), hardwareSpec, specs);
    }

    public PC withPhotos(List<String> allPhotos) {
        return new PC(id(), hardwareSpec(), specs(), allPhotos);
    }
}
