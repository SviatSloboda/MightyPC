package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record CPU(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int performance,
        int energyConsumption,
        List<String> cpuPhotos
) {
    public CPU(String id, HardwareSpec hardwareSpec, int performance, int energyConsumption) {
        this(id, hardwareSpec, performance, energyConsumption, Collections.emptyList());
    }

    public CPU(HardwareSpec hardwareSpec, int performance, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, performance, energyConsumption, Collections.emptyList());
    }

    public CPU withPhotos(List<String> allPhotos) {
        return new CPU(id(), hardwareSpec(), performance(), energyConsumption(), allPhotos);
    }
}
