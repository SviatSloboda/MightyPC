package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record SSD(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption,
        List<String> ssdPhotos
) {
    public SSD(String id, HardwareSpec hardwareSpec, int capacity, int energyConsumption) {
        this(id, hardwareSpec, capacity, energyConsumption, Collections.emptyList());
    }

    public SSD(HardwareSpec hardwareSpec, int capacity, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, capacity, energyConsumption, Collections.emptyList());
    }

    public SSD withPhotos(List<String> allPhotos) {
        return new SSD(id(), hardwareSpec(), capacity(), energyConsumption(), allPhotos);
    }
}
