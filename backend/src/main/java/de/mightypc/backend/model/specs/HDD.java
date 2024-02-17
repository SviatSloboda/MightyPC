package de.mightypc.backend.model.specs;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public record HDD(
        @Id
        String id,
        HardwareSpec hardwareSpec,
        int capacity,
        int energyConsumption
) {
    public HDD(HardwareSpec hardwareSpec, int capacity, int energyConsumption) {
        this(UUID.randomUUID().toString(), hardwareSpec, capacity, energyConsumption);
    }
}
