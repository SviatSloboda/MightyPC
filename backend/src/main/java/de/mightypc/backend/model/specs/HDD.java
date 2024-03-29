package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record HDD(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption,
        List<String> hddPhotos

) {
    public HDD(String id, HardwareSpec hardwareSpec, int capacity, int energyConsumption) {
        this(id, hardwareSpec, capacity, energyConsumption, Collections.emptyList());
    }

    public HDD(HardwareSpec hardwareSpec, int capacity, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, capacity, energyConsumption, Collections.emptyList());
    }

    public HDD withPhotos(List<String> allPhotos) {
        return new HDD(id(), hardwareSpec(), capacity(), energyConsumption(), allPhotos);
    }
}
