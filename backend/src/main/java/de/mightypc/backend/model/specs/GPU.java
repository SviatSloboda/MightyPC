package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record GPU(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int performance,
        int energyConsumption,
        List<String> gpuPhotos

) {
    public GPU(String id, HardwareSpec hardwareSpec, int performance, int energyConsumption) {
        this(id, hardwareSpec, performance, energyConsumption, Collections.emptyList());
    }

    public GPU(HardwareSpec hardwareSpec, int performance, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, performance, energyConsumption, Collections.emptyList());
    }

    public GPU withPhotos(List<String> allPhotos) {
        return new GPU(id(), hardwareSpec(), performance(), energyConsumption(), allPhotos);
    }
}
